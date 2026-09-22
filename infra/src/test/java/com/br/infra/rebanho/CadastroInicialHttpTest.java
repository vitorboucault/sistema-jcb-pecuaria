package com.br.infra.rebanho;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@Transactional
class CadastroInicialHttpTest {
    @Autowired private WebApplicationContext context;
    @Autowired private JdbcTemplate jdbc;
    @Autowired private EntityManager entityManager;
    private MockMvc mvc;
    private String token;

    @BeforeEach
    void configurar() {
        mvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(SecurityMockMvcConfigurers.springSecurity()).build();
        token = JWT.create().withIssuer("sistemajcb-auth").withSubject("dono")
                .withExpiresAt(Instant.now().plusSeconds(300))
                .sign(Algorithm.HMAC256("test-secret"));
    }

    private String payload(String brinco, String origem, String nascimento) {
        return payload(brinco, origem, nascimento, null, null);
    }

    private String payload(String brinco, String origem, String nascimento, Double pesoAtual, String dataPesagem) {
        String pesoJson = pesoAtual == null ? "null" : pesoAtual.toString();
        String dataPesagemJson = dataPesagem == null ? "null" : "\"" + dataPesagem + "\"";
        return """
                {"brincoRgd":"%s","dataNascimento":"%s","sexo":"FEMEA",
                 "categoria":"VACA","origem":"%s","pesoAtual":%s,"dataPesagem":%s}
                """.formatted(brinco, nascimento, origem, pesoJson, dataPesagemJson);
    }

    @ParameterizedTest
    @ValueSource(strings = {"COMPRA", "NASCIMENTO", "DESCONHECIDO"})
    @DisplayName("@spec:AC-330 @spec:AC-322 @spec:AC-325 @spec:AC-335 Cadastro autenticado persiste origem sem efeitos operacionais")
    void cadastraSemEfeitosOperacionais(String origem) throws Exception {
        String brinco = "HTTP-" + UUID.randomUUID();
        Long despesasAntes = jdbc.queryForObject("select count(*) from transacao_financeira", Long.class);
        Long pesagensAntes = jdbc.queryForObject("select count(*) from pesagem", Long.class);
        String resposta = mvc.perform(post("/api/v1/animais/cadastrar")
                        .header("Authorization", "Bearer " + token).contentType(MediaType.APPLICATION_JSON)
                        .content(payload(brinco, origem, "2020-05-10")))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        UUID id = UUID.fromString(resposta.replace("\"", ""));
        entityManager.flush();
        var animal = jdbc.queryForMap("select * from animal where id = ?", id);
        assertThat(animal).containsEntry("brinco_rgd", brinco).containsEntry("origem", origem)
                .containsEntry("status", "ATIVO").containsEntry("sexo", "FEMEA")
                .containsEntry("categoria_atual", "VACA");
        assertThat(animal.get("lote_atual_id")).isNull();
        assertThat(animal.get("mae_id")).isNull();
        assertThat(animal.get("data_morte")).isNull();
        assertThat(jdbc.queryForObject("select count(*) from transacao_financeira", Long.class)).isEqualTo(despesasAntes);
        assertThat(jdbc.queryForObject("select count(*) from pesagem", Long.class)).isEqualTo(pesagensAntes);
    }

    @Test
    @DisplayName("@spec:AC-334 @spec:AC-339 Cadastro com peso persiste pesagem de origem inicial")
    void cadastraPesoAtualComoPesagemInicial() throws Exception {
        String brinco = "PESO-" + UUID.randomUUID();
        String resposta = mvc.perform(post("/api/v1/animais/cadastrar")
                        .header("Authorization", "Bearer " + token).contentType(MediaType.APPLICATION_JSON)
                        .content(payload(brinco, "COMPRA", "2020-05-10", 385.5, "2026-09-20")))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        UUID id = UUID.fromString(resposta.replace("\"", ""));
        entityManager.flush();

        var animal = jdbc.queryForMap("select origem from animal where id = ?", id);
        var pesagem = jdbc.queryForMap(
                "select animal_id, data_pesagem, peso_kg, origem from pesagem where animal_id = ?", id
        );
        assertThat(animal).containsEntry("origem", "COMPRA");
        assertThat(pesagem.get("animal_id")).isEqualTo(id);
        assertThat(pesagem.get("data_pesagem").toString()).isEqualTo("2026-09-20");
        assertThat(((Number) pesagem.get("peso_kg")).doubleValue()).isEqualTo(385.5);
        assertThat(pesagem.get("origem")).isEqualTo("CADASTRO_INICIAL");
    }

    @Test
    @DisplayName("@spec:AC-330 Cadastro inicial exige autenticação sem persistir")
    void rejeitaSemAutenticacao() throws Exception {
        Long antes = jdbc.queryForObject("select count(*) from animal", Long.class);
        mvc.perform(post("/api/v1/animais/cadastrar").contentType(MediaType.APPLICATION_JSON)
                        .content(payload("SEM-AUTH", "COMPRA", "2020-05-10")))
                .andExpect(status().isUnauthorized());
        assertThat(jdbc.queryForObject("select count(*) from animal", Long.class)).isEqualTo(antes);
    }

    @Test
    @DisplayName("@spec:AC-330 @spec:AC-328 Handler global responde por campo inválido sem persistir")
    void rejeitaPayloadInvalido() throws Exception {
        Long antes = jdbc.queryForObject("select count(*) from animal", Long.class);
        mvc.perform(post("/api/v1/animais/cadastrar").header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON).content(payload("", "COMPRA", "2020-05-10")))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.erro").value("Dados inválidos"))
                .andExpect(jsonPath("$.campos.brincoRgd").isString());
        assertThat(jdbc.queryForObject("select count(*) from animal", Long.class)).isEqualTo(antes);
    }

    @Test
    @DisplayName("@spec:AC-330 @spec:AC-324 Data futura usa mensagem genérica no handler global")
    void rejeitaNascimentoFuturo() throws Exception {
        Long antes = jdbc.queryForObject("select count(*) from animal", Long.class);
        mvc.perform(post("/api/v1/animais/cadastrar").header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload("FUTURO", "COMPRA", LocalDate.now().plusDays(1).toString())))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensagem").value("A data de nascimento não pode ser futura."));
        assertThat(jdbc.queryForObject("select count(*) from animal", Long.class)).isEqualTo(antes);
    }

    @Test
    @DisplayName("@spec:AC-330 @spec:AC-323 Brinco duplicado retorna erro de negócio sem novo animal")
    void rejeitaDuplicado() throws Exception {
        String body = payload("DUP-" + UUID.randomUUID(), "COMPRA", "2020-05-10");
        mvc.perform(post("/api/v1/animais/cadastrar").header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON).content(body)).andExpect(status().isCreated());
        entityManager.flush();
        Long antes = jdbc.queryForObject("select count(*) from animal", Long.class);
        mvc.perform(post("/api/v1/animais/cadastrar").header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.erro").value("Regra de negócio inválida"));
        assertThat(jdbc.queryForObject("select count(*) from animal", Long.class)).isEqualTo(antes);
    }

    @Test
    @DisplayName("@spec:AC-333 @spec:AC-201 @spec:AC-202 Morte e reversão preservam lote que foi encerrado")
    void reverteSemReabrirLote() throws Exception {
        UUID loteId = UUID.randomUUID();
        jdbc.update("insert into lote (id, nome, fase_lote, data_formacao) values (?, 'HISTORICO', 'CRIA', ?)",
                loteId, LocalDate.now().minusYears(1));
        String body = payload("LOTE-" + UUID.randomUUID(), "COMPRA", "2020-05-10")
                .replace("\"origem\"", "\"loteId\":\"" + loteId + "\",\"origem\"");
        String resposta = mvc.perform(post("/api/v1/animais/cadastrar").header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        UUID id = UUID.fromString(resposta.replace("\"", ""));
        mvc.perform(delete("/api/v1/animais/{id}/baixa-morte", id)
                        .param("dataMorte", LocalDate.now().toString()).header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());
        entityManager.flush();
        assertThat(jdbc.queryForMap("select status, lote_atual_id from animal where id = ?", id))
                .containsEntry("status", "MORTO").containsEntry("lote_atual_id", loteId);
        jdbc.update("update lote set data_encerramento = ? where id = ?", LocalDate.now(), loteId);
        mvc.perform(post("/api/v1/animais/{id}/reverter-morte", id).header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());
        entityManager.flush();
        assertThat(jdbc.queryForMap("select status, lote_atual_id, data_morte from animal where id = ?", id))
                .containsEntry("status", "ATIVO").containsEntry("lote_atual_id", loteId).containsEntry("data_morte", null);
        assertThat(jdbc.queryForObject("select data_encerramento from lote where id = ?", LocalDate.class, loteId))
                .isEqualTo(LocalDate.now());
    }
}
