package com.br.infra.persistence.migration;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.testcontainers.postgresql.PostgreSQLContainer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

/** Executado por check-postgres.sh; Docker ausente é erro, nunca skip. */
class RebanhoPostgresIT {
    private Flyway migrador(PostgreSQLContainer banco, String alvo) {
        return Flyway.configure().dataSource(banco.getJdbcUrl(), banco.getUsername(), banco.getPassword())
                .locations("classpath:db/migration").target(alvo).load();
    }

    @Test
    @DisplayName("@spec:AC-332 Banco PostgreSQL vazio recebe todas as migrations e constraints de origem")
    void migraBancoVazio() throws Exception {
        try (var banco = new PostgreSQLContainer("postgres:17-alpine")) {
            banco.start();
            var flyway = migrador(banco, "latest");
            assertThat(flyway.migrate().migrationsExecuted).isEqualTo(11);
            flyway.validate();
            try (var conexao = DriverManager.getConnection(banco.getJdbcUrl(), banco.getUsername(), banco.getPassword())) {
                for (String origem : new String[]{"COMPRA", "NASCIMENTO", "DESCONHECIDO"}) {
                    inserir(conexao, origem, "'" + origem + "'");
                }
                assertThatThrownBy(() -> inserir(conexao, "INVALIDO", "'CADASTRO_INICIAL'"))
                        .isInstanceOf(SQLException.class).extracting("SQLState").isEqualTo("23514");
                assertThatThrownBy(() -> inserir(conexao, "NULO", "NULL"))
                        .isInstanceOf(SQLException.class).extracting("SQLState").isEqualTo("23502");
                try (var stmt = conexao.createStatement(); var rs = stmt.executeQuery("select count(*) from animal")) {
                    assertThat(rs.next()).isTrue();
                    assertThat(rs.getInt(1)).isEqualTo(3);
                }
            }
        }
    }

    @Test
    @DisplayName("@spec:AC-353 @spec:AC-356 V10 restringe aquisição histórica e remove-a em cascata")
    void criaAquisicaoComRestriçõesECascata() throws Exception {
        try (var banco = new PostgreSQLContainer("postgres:17-alpine")) {
            banco.start();
            var flyway = migrador(banco, "latest");
            assertThat(flyway.migrate().migrationsExecuted).isEqualTo(11);

            UUID animalId = UUID.randomUUID();
            try (var conexao = DriverManager.getConnection(banco.getJdbcUrl(), banco.getUsername(), banco.getPassword())) {
                try (var stmt = conexao.prepareStatement("""
                        insert into animal (id, brinco_rgd, data_nascimento, sexo, categoria_atual, status, origem)
                        values (?, 'AQUISICAO-MIGRATION', '2019-04-10', 'FEMEA', 'VACA', 'ATIVO', 'COMPRA')
                        """)) {
                    stmt.setObject(1, animalId);
                    stmt.executeUpdate();
                }

                try (var stmt = conexao.prepareStatement("""
                        insert into aquisicao_animal (id, animal_id, data_aquisicao, valor_aquisicao)
                        values (?, ?, '2021-05-10', 3200.00)
                        """)) {
                    stmt.setObject(1, UUID.randomUUID());
                    stmt.setObject(2, animalId);
                    stmt.executeUpdate();
                }

                assertThatThrownBy(() -> conexao.createStatement().executeUpdate("""
                        insert into aquisicao_animal (id, animal_id, valor_aquisicao)
                        values (uuid_generate_v4(), '%s', 0)
                        """.formatted(animalId)))
                        .isInstanceOf(SQLException.class).extracting("SQLState").isEqualTo("23514");
                assertThatThrownBy(() -> conexao.createStatement().executeUpdate("""
                        insert into aquisicao_animal (id, animal_id)
                        values (uuid_generate_v4(), '%s')
                        """.formatted(animalId)))
                        .isInstanceOf(SQLException.class).extracting("SQLState").isEqualTo("23514");

                try (var stmt = conexao.prepareStatement("delete from animal where id = ?")) {
                    stmt.setObject(1, animalId);
                    stmt.executeUpdate();
                }
                try (var rs = conexao.createStatement().executeQuery("select count(*) from aquisicao_animal")) {
                    assertThat(rs.next()).isTrue();
                    assertThat(rs.getInt(1)).isZero();
                }
            }
        }
    }

    @Test
    @DisplayName("@spec:AC-332 @spec:AC-318 V8 para V9 preserva legado com origem DESCONHECIDO")
    void migraLegadoV8() throws Exception {
        try (var banco = new PostgreSQLContainer("postgres:17-alpine")) {
            banco.start();
            migrador(banco, "8").migrate();
            try (var conexao = DriverManager.getConnection(banco.getJdbcUrl(), banco.getUsername(), banco.getPassword());
                 var stmt = conexao.createStatement()) {
                stmt.executeUpdate("""
                        insert into animal (brinco_rgd, data_nascimento, sexo, categoria_atual, status)
                        values ('LEGADO', '2020-05-10', 'FEMEA', 'VACA', 'ATIVO')
                        """);
                assertThat(migrador(banco, "latest").migrate().migrationsExecuted).isEqualTo(3);
                migrador(banco, "latest").validate();
                try (var rs = stmt.executeQuery("select origem, data_nascimento, categoria_atual from animal where brinco_rgd = 'LEGADO'")) {
                    assertThat(rs.next()).isTrue();
                    assertThat(rs.getString("origem")).isEqualTo("DESCONHECIDO");
                    assertThat(rs.getDate("data_nascimento").toLocalDate()).isEqualTo("2020-05-10");
                    assertThat(rs.getString("categoria_atual")).isEqualTo("VACA");
                }
                stmt.executeUpdate("""
                        insert into animal (brinco_rgd, data_nascimento, sexo, categoria_atual, status)
                        values ('SEM-ORIGEM', '2021-05-10', 'FEMEA', 'NOVILHA', 'ATIVO')
                        """);
                try (var rs = stmt.executeQuery("select origem from animal where brinco_rgd = 'SEM-ORIGEM'")) {
                    assertThat(rs.next()).isTrue();
                    assertThat(rs.getString(1)).isEqualTo("DESCONHECIDO");
                }
            }
        }
    }

    @Test
    @DisplayName("@spec:AC-369 V11 permite nascimento nulo e preserva datas legadas")
    void v11PermiteNascimentoNuloSemAlterarRegistrosExistentes() throws Exception {
        try (var banco = new PostgreSQLContainer("postgres:17-alpine")) {
            banco.start();
            var flyway = migrador(banco, "latest");
            assertThat(flyway.migrate().migrationsExecuted).isEqualTo(11);

            try (var conexao = DriverManager.getConnection(banco.getJdbcUrl(), banco.getUsername(), banco.getPassword())) {
                try (var stmt = conexao.prepareStatement("""
                        insert into animal (id, brinco_rgd, data_nascimento, sexo, categoria_atual, status, origem)
                        values (?, ?, ?, 'FEMEA', 'VACA', 'ATIVO', 'DESCONHECIDO')
                        """)) {
                    stmt.setObject(1, UUID.randomUUID());
                    stmt.setString(2, "V11-COM-DATA");
                    stmt.setDate(3, java.sql.Date.valueOf("2020-05-10"));
                    stmt.executeUpdate();
                }
                try (var stmt = conexao.prepareStatement("""
                        insert into animal (id, brinco_rgd, data_nascimento, sexo, categoria_atual, status, origem)
                        values (?, ?, NULL, 'FEMEA', 'VACA', 'ATIVO', 'DESCONHECIDO')
                        """)) {
                    stmt.setObject(1, UUID.randomUUID());
                    stmt.setString(2, "V11-SEM-DATA");
                    stmt.executeUpdate();
                }

                try (var stmt = conexao.prepareStatement("select data_nascimento from animal where brinco_rgd = ?")) {
                    stmt.setString(1, "V11-COM-DATA");
                    try (var rs = stmt.executeQuery()) {
                        assertThat(rs.next()).isTrue();
                        assertThat(rs.getDate(1).toLocalDate()).isEqualTo("2020-05-10");
                    }
                    stmt.setString(1, "V11-SEM-DATA");
                    try (var rs = stmt.executeQuery()) {
                        assertThat(rs.next()).isTrue();
                        assertThat(rs.getDate(1)).isNull();
                    }
                }
            }
        }
    }

    private void inserir(Connection conexao, String brinco, String origemSql) throws SQLException {
        try (var stmt = conexao.createStatement()) {
            stmt.executeUpdate("""
                    insert into animal (brinco_rgd, data_nascimento, sexo, categoria_atual, status, origem)
                    values ('%s', '2020-05-10', 'FEMEA', 'VACA', 'ATIVO', %s)
                    """.formatted(brinco, origemSql));
        }
    }
}
