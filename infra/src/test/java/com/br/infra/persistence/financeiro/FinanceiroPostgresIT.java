package com.br.infra.persistence.financeiro;

import com.br.core.domain.repository.DespesaRepository;
import com.br.core.domain.repository.LancamentoFinanceiroRepository;
import com.br.infra.persistence.repository.SpringDataDespesaRepository;
import com.br.usecase.manejo.RegistrarCompraAnimalUseCase;
import jakarta.persistence.EntityManager;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.postgresql.PostgreSQLContainer;

import java.sql.DriverManager;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.*;

/** Executa também toda a suíte de persistência em PostgreSQL migrado, sem ddl-auto gerando schema. */
@SpringBootTest(properties = {
        "spring.flyway.enabled=true", "spring.flyway.baseline-on-migrate=false",
        "spring.jpa.hibernate.ddl-auto=validate", "spring.jpa.show-sql=false"
})
class FinanceiroPostgresIT extends FinanceiroPersistenceTest {
    private static final PostgreSQLContainer BANCO = iniciarBanco();

    @Autowired
    FinanceiroPostgresIT(LancamentoFinanceiroRepository financeiro, DespesaRepository despesas,
                        SpringDataDespesaRepository jpa,
                        EntityManager em, JdbcTemplate jdbc, RegistrarCompraAnimalUseCase compra) {
        super(financeiro, despesas, jpa, em, jdbc, compra);
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

    @Test
    @DisplayName("@spec:AC-700 @spec:AC-712 PostgreSQL vazio recebe V1–V12 e rejeita novos registros incompletos")
    void migraBancoVazioEValidaConstraints() throws Exception {
        var flyway = migrador(BANCO, "latest");
        flyway.validate();
        assertThat(flyway.info().current().getVersion().getVersion()).isEqualTo("12");
        assertThat(flyway.info().applied()).hasSize(12);
        assertThat(flyway.migrate().migrationsExecuted).isZero();
        try (var conexao = DriverManager.getConnection(BANCO.getJdbcUrl(), BANCO.getUsername(), BANCO.getPassword());
             var stmt = conexao.createStatement()) {
            try (var rs = stmt.executeQuery("select count(*) from transacao_financeira")) {
                assertThat(rs.next()).isTrue();
                assertThat(rs.getLong(1)).isZero();
            }
            String insert = """
                    insert into transacao_financeira (tipo, data_transacao, valor, categoria, descricao, situacao, data_efetivacao, tipo_centro_custo)
                    values ('DESPESA', '2025-12-15', 10.00, 'ENERGIA', %s, %s, %s, 'GERAL')
                    """;
            for (String[] valores : new String[][]{
                    {"NULL", "'PENDENTE'", "NULL", "23502"},
                    {"' '", "'PENDENTE'", "NULL", "23514"},
                    {"'Energia'", "NULL", "NULL", "23502"},
                    {"'Energia'", "'INVALIDO'", "NULL", "23514"},
                    {"'Energia'", "'PENDENTE'", "'2025-12-20'", "23514"},
                    {"'Energia'", "'EFETIVADO'", "NULL", "23514"}
            }) {
                assertThatThrownBy(() -> stmt.executeUpdate(insert.formatted(valores[0], valores[1], valores[2])))
                        .isInstanceOf(SQLException.class).extracting("SQLState").isEqualTo(valores[3]);
            }
        }
    }

    private Flyway migrador(PostgreSQLContainer banco, String alvo) {
        return Flyway.configure().dataSource(banco.getJdbcUrl(), banco.getUsername(), banco.getPassword())
                .locations("classpath:db/migration").target(alvo).load();
    }
}
