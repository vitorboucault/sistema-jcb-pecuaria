package com.br.infra.financeiro;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.MediaType;
import org.testcontainers.postgresql.PostgreSQLContainer;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

/** Executa os contratos HTTP com JWT e PostgreSQL criado exclusivamente por Flyway. */
@SpringBootTest(properties = {
        "spring.flyway.enabled=true", "spring.flyway.baseline-on-migrate=false",
        "spring.jpa.hibernate.ddl-auto=validate", "spring.jpa.show-sql=false"
})
class FinanceiroHttpPostgresIT extends FinanceiroHttpTest {
    private static final PostgreSQLContainer BANCO = iniciarBanco();

    @Autowired
    FinanceiroHttpPostgresIT(WebApplicationContext context, JdbcTemplate jdbc, EntityManager entityManager) {
        super(context, jdbc, entityManager);
    }

    private static PostgreSQLContainer iniciarBanco() {
        var banco = new PostgreSQLContainer("postgres:17-alpine");
        banco.start();
        return banco;
    }

    @DynamicPropertySource
    static void configurarBanco(DynamicPropertyRegistry properties) {
        properties.add("spring.datasource.url", BANCO::getJdbcUrl);
        properties.add("spring.datasource.username", BANCO::getUsername);
        properties.add("spring.datasource.password", BANCO::getPassword);
        properties.add("spring.datasource.driver-class-name", () -> "org.postgresql.Driver");
    }

    @AfterAll
    static void encerrarBanco() {
        BANCO.stop();
    }

    @ParameterizedTest
    @CsvSource({"DESPESA,true", "RECEITA,true", "DESPESA,false", "RECEITA,false"})
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    @DisplayName("@spec:AC-703 @spec:AC-704 PostgreSQL serializa confirmações concorrentes com transações HTTP independentes")
    void confirmaConcorrentemente(String tipo, boolean datasDiferentes) throws Exception {
        var id = inserirPendente(tipo);
        var antes = snapshot(id);
        String acao = tipo.equals("DESPESA") ? "pagar" : "receber";
        String dataA = "2025-01-11";
        String dataB = datasDiferentes ? "2025-01-12" : dataA;
        try (var executor = Executors.newFixedThreadPool(2)) {
            try (var bloqueio = jdbc.getDataSource().getConnection()) {
                bloqueio.setAutoCommit(false);
                try {
                    try (var stmt = bloqueio.prepareStatement("select id from transacao_financeira where id=? for update")) {
                        stmt.setObject(1, id);
                        try (var rs = stmt.executeQuery()) {
                            assertThat(rs.next()).isTrue();
                        }
                    }
                    var prontas = new CountDownLatch(2);
                    var iniciar = new CountDownLatch(1);
                    var primeira = executor.submit(() -> {
                        prontas.countDown();
                        assertThat(iniciar.await(5, TimeUnit.SECONDS)).isTrue();
                        return confirmarConcorrente(id, acao, dataA);
                    });
                    var segunda = executor.submit(() -> {
                        prontas.countDown();
                        assertThat(iniciar.await(5, TimeUnit.SECONDS)).isTrue();
                        return confirmarConcorrente(id, acao, dataB);
                    });
                    assertThat(prontas.await(5, TimeUnit.SECONDS)).isTrue();
                    iniciar.countDown();
                    aguardarLeiturasBloqueadas();
                    assertThat(primeira.isDone()).isFalse();
                    assertThat(segunda.isDone()).isFalse();
                    bloqueio.commit();
                    int statusA = primeira.get(10, TimeUnit.SECONDS);
                    int statusB = segunda.get(10, TimeUnit.SECONDS);
                    assertThat(List.of(statusA, statusB)).containsExactlyInAnyOrder(200, datasDiferentes ? 409 : 200);
                    var depois = snapshot(id);
                    assertThat(depois.get("situacao")).isEqualTo("EFETIVADO");
                    assertThat(depois.get("data_efetivacao"))
                            .isEqualTo(java.sql.Date.valueOf(LocalDate.parse(statusA == 200 ? dataA : dataB)));
                    assertThat(semEfetivacao(depois)).isEqualTo(semEfetivacao(antes));
                    assertThat(jdbc.queryForObject("select count(*) from transacao_financeira where id=?", Long.class, id)).isEqualTo(1);
                } finally {
                    // Libera o bloqueio também em falha, antes de aguardar o encerramento das threads.
                    bloqueio.rollback();
                }
            }
        } finally {
            jdbc.update("delete from transacao_financeira where id=?", id);
            jdbc.update("delete from animal where id=?", antes.get("centro_custo_id"));
        }
    }

    private int confirmarConcorrente(java.util.UUID id, String acao, String data) throws Exception {
        return mvc.perform(post("/api/v1/financeiro/lancamentos/{id}/" + acao, id)
                        .header("Authorization", "Bearer " + token).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"dataEfetivacao\":\"" + data + "\"}"))
                .andReturn().getResponse().getStatus();
    }

    private void aguardarLeiturasBloqueadas() throws Exception {
        long limite = System.nanoTime() + TimeUnit.SECONDS.toNanos(10);
        while (System.nanoTime() < limite) {
            Long bloqueadas = jdbc.queryForObject("""
                    select count(*) from pg_stat_activity
                    where datname=current_database() and wait_event_type='Lock'
                      and lower(ltrim(query)) like 'select%'
                      and query like '%transacao_financeira%'
                    """, Long.class);
            if (bloqueadas != null && bloqueadas >= 2) {
                return;
            }
            Thread.sleep(20);
        }
        throw new AssertionError("As duas consultas de confirmação deveriam aguardar o bloqueio da linha.");
    }
}
