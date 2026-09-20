package com.br.infra.persistence.migration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

class OrigemAnimalMigrationTest {

    @Test
    @DisplayName("@spec:AC-316 Migration restringe a origem aos valores do domínio")
    void migrationDefineOrigemComFallbackEConjuntoFechado() throws IOException {
        try (InputStream migration = getClass().getClassLoader()
                .getResourceAsStream("db/migration/V9__Add_Origem_Animal.sql")) {
            assertThat(migration).isNotNull();
            String sql = new String(migration.readAllBytes(), StandardCharsets.UTF_8);

            assertThat(sql).contains("ADD COLUMN origem VARCHAR(20) NOT NULL DEFAULT 'DESCONHECIDO'");
            assertThat(sql).contains("CHECK (origem IN ('COMPRA', 'NASCIMENTO', 'DESCONHECIDO'))");
        }
    }
}
