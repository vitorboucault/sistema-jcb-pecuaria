package com.br.infra.financeiro;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.postgresql.PostgreSQLContainer;

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
}
