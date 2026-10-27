package com.br.infra.financeiro;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
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
import com.br.core.domain.enums.TipoDeCusto;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.UUID;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@Transactional
public class FinanceiroHttpTest {
    private static final String URL = "/api/v1/financeiro/lancamentos";
    private final WebApplicationContext context;
    protected final JdbcTemplate jdbc;
    private final EntityManager entityManager;
    protected MockMvc mvc;
    protected String token;

    @Autowired
    public FinanceiroHttpTest(WebApplicationContext context, JdbcTemplate jdbc, EntityManager entityManager) {
        this.context = context;
        this.jdbc = jdbc;
        this.entityManager = entityManager;
    }

    @BeforeEach
    void configurar() {
        mvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(SecurityMockMvcConfigurers.springSecurity()).build();
        token = token(Instant.now().plusSeconds(300));
    }

    @ParameterizedTest
    @CsvSource({"DESPESA,pagar", "RECEITA,receber"})
    @DisplayName("@spec:AC-703 API confirma integralmente preservando todos os atributos e a origem operacional")
    void confirmaIntegralmente(String tipo, String acao) throws Exception {
        UUID id = inserirPendente(tipo);
        var antes = snapshot(id);
        mvc.perform(post(URL + "/{id}/" + acao, id).header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"dataEfetivacao\":\"2025-01-11\"}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.situacao").value("EFETIVADO"))
                .andExpect(jsonPath("$.dataEfetivacao").value("2025-01-11"))
                .andExpect(jsonPath("$.valor").value(100.00));
        entityManager.flush();
        entityManager.clear();
        mvc.perform(get(URL + "/{id}", id).header("Authorization", "Bearer " + token))
                .andExpect(status().isOk()).andExpect(jsonPath("$.situacao").value("EFETIVADO"))
                .andExpect(jsonPath("$.dataEfetivacao").value("2025-01-11"));
        var depois = snapshot(id);
        assertThat(depois.get("situacao")).isEqualTo("EFETIVADO");
        assertThat(depois.get("data_efetivacao")).isEqualTo(java.sql.Date.valueOf("2025-01-11"));
        assertThat(semEfetivacao(depois)).isEqualTo(semEfetivacao(antes));
    }

    @ParameterizedTest
    @CsvSource({"DESPESA,pagar", "RECEITA,receber"})
    @DisplayName("@spec:AC-704 Mesma data retorna 200; outra data retorna 409 sem alterar o lançamento efetivado")
    void repeteConfirmacao(String tipo, String acao) throws Exception {
        UUID id = inserirPendente(tipo);
        String primeira = confirmar(id, acao, "{\"dataEfetivacao\":\"2025-01-11\"}", 200);
        entityManager.flush();
        var antes = snapshot(id);
        String repetida = confirmar(id, acao, "{\"dataEfetivacao\":\"2025-01-11\"}", 200);
        assertThat(repetida).isEqualTo(primeira);
        confirmar(id, acao, "{\"dataEfetivacao\":\"2025-01-12\"}", 409);
        assertThat(snapshot(id)).isEqualTo(antes);
    }

    @ParameterizedTest
    @CsvSource({"DESPESA,receber,PENDENTE", "RECEITA,pagar,PENDENTE",
            "DESPESA,receber,EFETIVADO", "RECEITA,pagar,EFETIVADO"})
    @DisplayName("@spec:AC-704 Tipo incompatível retorna 409 antes de qualquer alteração")
    void rejeitaConfirmacaoDeTipoErrado(String tipo, String acao, String situacao) throws Exception {
        UUID id = inserirPendente(tipo);
        if (situacao.equals("EFETIVADO")) {
            jdbc.update("update transacao_financeira set situacao='EFETIVADO', data_efetivacao='2025-01-11' where id=?", id);
        }
        var antes = snapshot(id);
        confirmar(id, acao, "{\"dataEfetivacao\":\"2025-01-11\"}", 409);
        assertThat(snapshot(id)).isEqualTo(antes);
    }

    @ParameterizedTest
    @MethodSource("confirmacoesInvalidas")
    @DisplayName("@spec:AC-703 API rejeita data inválida e campos de pagamento parcial sem alterar nenhum dado")
    void rejeitaConfirmacaoInvalida(String tipo, String acao, String payload) throws Exception {
        UUID id = inserirPendente(tipo);
        var antes = snapshot(id);
        confirmar(id, acao, payload, 400);
        assertThat(snapshot(id)).isEqualTo(antes);
    }

    static Stream<org.junit.jupiter.params.provider.Arguments> confirmacoesInvalidas() {
        return Stream.of("DESPESA", "RECEITA").flatMap(tipo -> Stream.of(
                "{}", "{\"dataEfetivacao\":null}", "{\"dataEfetivacao\":\"inválida\"}",
                "{\"dataEfetivacao\":\"2025-02-30\"}",
                "{\"dataEfetivacao\":\"" + LocalDate.now(ZoneId.of("America/Bahia")).plusDays(2) + "\"}",
                "{\"dataEfetivacao\":\"2025-01-11\",\"valor\":50.00}",
                "{\"dataEfetivacao\":\"2025-01-11\",\"valorPago\":50.00}",
                "{\"dataEfetivacao\":\"2025-01-11\",\"versao\":1}"
        ).map(body -> org.junit.jupiter.params.provider.Arguments.of(tipo, tipo.equals("DESPESA") ? "pagar" : "receber", body)));
    }

    @ParameterizedTest
    @ValueSource(strings = {"pagar", "receber"})
    @DisplayName("@spec:AC-703 Confirmação de UUID inexistente é 404 e UUID inválido é 400")
    void confirmaUuidInexistenteOuInvalido(String acao) throws Exception {
        confirmar(UUID.randomUUID(), acao, "{\"dataEfetivacao\":\"2025-01-11\"}", 404);
        mvc.perform(post(URL + "/inválido/" + acao).header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"dataEfetivacao\":\"2025-01-11\"}"))
                .andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    @CsvSource({"DESPESA,pagar", "RECEITA,receber"})
    @DisplayName("@spec:AC-703 Confirmação exige JWT válido sem alteração não autorizada")
    void protegeConfirmacao(String tipo, String acao) throws Exception {
        UUID id = inserirPendente(tipo);
        var antes = snapshot(id);
        for (String credencial : new String[]{null, "inválido", token(Instant.now().minusSeconds(60))}) {
            var request = post(URL + "/{id}/" + acao, id).contentType(MediaType.APPLICATION_JSON)
                    .content("{\"dataEfetivacao\":\"2025-01-11\"}");
            if (credencial != null) {
                request.header("Authorization", "Bearer " + credencial);
            }
            mvc.perform(request).andExpect(status().isUnauthorized());
        }
        assertThat(snapshot(id)).isEqualTo(antes);
    }

    protected UUID inserirPendente(String tipo) {
        UUID centro = UUID.randomUUID();
        jdbc.update("""
                insert into animal (id, brinco_rgd, data_nascimento, sexo, categoria_atual, status, origem)
                values (?, ?, '2020-01-01', 'FEMEA', 'VACA', 'ATIVO', 'COMPRA')
                """, centro, "CONF-" + centro);
        UUID id = UUID.randomUUID();
        jdbc.update("""
                insert into transacao_financeira
                (id,tipo,data_transacao,valor,categoria,centro_custo_id,tipo_centro_custo,descricao,situacao,
                 data_vencimento,origem_tipo,origem_id)
                values (?,?, '2025-01-10',100.00,?,?,'ANIMAL','Confirmação integral','PENDENTE', '2025-01-15',?,?)
                """, id, tipo, tipo.equals("DESPESA") ? "COMPRA_ANIMAL" : "VENDA_LEITE", centro,
                tipo.equals("DESPESA") ? "COMPRA_ANIMAL" : "VENDA", centro);
        return id;
    }

    protected Map<String, Object> snapshot(UUID id) {
        return jdbc.queryForMap("select * from transacao_financeira where id=?", id);
    }

    protected Map<String, Object> semEfetivacao(Map<String, Object> original) {
        var copia = new LinkedHashMap<>(original);
        copia.remove("situacao");
        copia.remove("data_efetivacao");
        return copia;
    }

    private String confirmar(UUID id, String acao, String payload, int statusEsperado) throws Exception {
        return mvc.perform(post(URL + "/{id}/" + acao, id).header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON).content(payload))
                .andExpect(status().is(statusEsperado)).andReturn().getResponse().getContentAsString();
    }

    @ParameterizedTest
    @CsvSource({"DESPESA,ENERGIA,PENDENTE", "RECEITA,VENDA_LEITE,PENDENTE",
            "DESPESA,ENERGIA,EFETIVADO", "RECEITA,VENDA_LEITE,EFETIVADO"})
    @DisplayName("@spec:AC-700 API autenticada cadastra e consulta receita/despesa com dados preservados")
    void cadastraEConsulta(String tipo, String categoria, String situacao) throws Exception {
        String efetivacao = situacao.equals("EFETIVADO") ? "\"2025-01-11\"" : "null";
        String body = payload(tipo, categoria, situacao, efetivacao, "GERAL", "null");
        var criacao = mvc.perform(post(URL).header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.descricao").value("Operação financeira"))
                .andExpect(jsonPath("$.tipo").value(tipo))
                .andExpect(jsonPath("$.categoria").value(categoria))
                .andExpect(jsonPath("$.situacao").value(situacao))
                .andExpect(jsonPath("$.valor").value(150.50))
                .andExpect(jsonPath("$.dataReferencia").value("2025-01-10"))
                .andExpect(jsonPath("$.dataVencimento").value("2025-01-15"))
                .andExpect(jsonPath("$.tipoDeCusto").value("GERAL"))
                .andReturn();
        String resposta = criacao.getResponse().getContentAsString();
        entityManager.flush();
        entityManager.clear();
        UUID id = jdbc.queryForObject("select id from transacao_financeira where descricao = ?", UUID.class, "Operação financeira");
        assertThat(criacao.getResponse().getHeader("Location")).isEqualTo(URL + "/" + id);
        mvc.perform(get(URL + "/{id}", id).header("Authorization", "Bearer " + token))
                .andExpect(status().isOk()).andExpect(content().json(resposta));
        assertThat(jdbc.queryForObject("select count(*) from transacao_financeira", Long.class)).isEqualTo(1);
        assertThat(jdbc.queryForObject("select origem_tipo from transacao_financeira where id = ?", String.class, id)).isNull();
        assertThat(jdbc.queryForObject("select origem_id from transacao_financeira where id = ?", UUID.class, id)).isNull();
    }

    @ParameterizedTest
    @EnumSource(value = TipoDeCusto.class, names = {"ANIMAL", "LOTE", "PASTO"})
    @DisplayName("@spec:AC-701 Cadastro aceita centro existente consultando os repositórios reais")
    void cadastraComCentroExistente(TipoDeCusto centro) throws Exception {
        UUID id = UUID.randomUUID();
        switch (centro) {
            case ANIMAL -> jdbc.update("""
                    insert into animal (id, brinco_rgd, data_nascimento, sexo, categoria_atual, status, origem)
                    values (?, ?, '2020-01-01', 'FEMEA', 'VACA', 'ATIVO', 'COMPRA')
                    """, id, "FIN-" + id);
            case LOTE -> jdbc.update("""
                    insert into lote (id, nome, fase_lote, data_formacao) values (?, 'Lote financeiro', 'RECRIA', '2025-01-01')
                    """, id);
            case PASTO -> jdbc.update("""
                    insert into pasto (id, nome, area_hectares, capacidade_suporte_ua, status_atual)
                    values (?, 'Pasto financeiro', 10, 2, 'DESCANSO')
                    """, id);
            default -> throw new AssertionError(centro);
        }
        mvc.perform(post(URL).header("Authorization", "Bearer " + token).contentType(MediaType.APPLICATION_JSON)
                        .content(payload("DESPESA", "ENERGIA", "PENDENTE", "null", centro.name(), "\"" + id + "\"")))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.referenciaId").value(id.toString()))
                .andExpect(jsonPath("$.tipoDeCusto").value(centro.name()));
    }

    @ParameterizedTest
    @EnumSource(value = TipoDeCusto.class, names = {"ANIMAL", "LOTE", "PASTO"})
    @DisplayName("@spec:AC-701 Centro inexistente retorna 400 sem criar lançamento")
    void rejeitaCentroInexistente(TipoDeCusto centro) throws Exception {
        mvc.perform(post(URL).header("Authorization", "Bearer " + token).contentType(MediaType.APPLICATION_JSON)
                        .content(payload("DESPESA", "ENERGIA", "PENDENTE", "null", centro.name(), "\"" + UUID.randomUUID() + "\"")))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.status").value(400));
        assertThat(jdbc.queryForObject("select count(*) from transacao_financeira", Long.class)).isZero();
    }

    @ParameterizedTest
    @MethodSource("payloadsInvalidos")
    @DisplayName("@spec:AC-701 API rejeita valores, categorias, datas e centros inválidos sem persistir")
    void rejeitaPayloadInvalido(String body) throws Exception {
        mvc.perform(post(URL).header("Authorization", "Bearer " + token).contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.status").value(400));
        assertThat(jdbc.queryForObject("select count(*) from transacao_financeira", Long.class)).isZero();
    }

    static Stream<String> payloadsInvalidos() {
        String valido = payload("DESPESA", "ENERGIA", "PENDENTE", "null", "GERAL", "null");
        return Stream.of(
                valido.replace("150.50", "null"), valido.replace("150.50", "0"),
                valido.replace("150.50", "-1"), valido.replace("150.50", "10000000000.00"),
                valido.replace("150.50", "1.001"), valido.replace("150.50", "\"inválido\""),
                valido.replace("ENERGIA", "VENDA_LEITE"), valido.replace("ENERGIA", "INVALIDA"),
                valido.replace("DESPESA", "INVALIDO"), valido.replace("PENDENTE", "INVALIDA"),
                valido.replace("\"2025-01-10\"", "null"), valido.replace("2025-01-10", "data-inválida"),
                valido.replace("Operação financeira", "  "), valido.replace("\"categoria\":\"ENERGIA\"", "\"categoria\":null"),
                valido.replace("\"situacao\":\"PENDENTE\"", "\"situacao\":null"),
                payload("DESPESA", "ENERGIA", "EFETIVADO", "null", "GERAL", "null"),
                payload("DESPESA", "ENERGIA", "PENDENTE", "\"2025-01-11\"", "GERAL", "null"),
                payload("DESPESA", "ENERGIA", "EFETIVADO", "\"" + LocalDate.now(ZoneId.of("America/Bahia")).plusDays(2) + "\"", "GERAL", "null"),
                payload("DESPESA", "ENERGIA", "PENDENTE", "null", "GERAL", "\"" + UUID.randomUUID() + "\""),
                payload("DESPESA", "ENERGIA", "PENDENTE", "null", "ANIMAL", "null"),
                valido.replace("\"tipoDeCusto\":\"GERAL\"", "\"tipoDeCusto\":null"),
                valido.replace("\"referenciaId\":null", "\"referenciaId\":\"inválido\""),
                "{}", "{"
        );
    }

    @Test
    @DisplayName("@spec:AC-700 UUID e origem enviados pelo cliente não definem a identidade ou origem persistida")
    void clienteNaoDefineIdentidadeOuOrigem() throws Exception {
        UUID idCliente = UUID.randomUUID();
        String body = payload("RECEITA", "VENDA_ANIMAIS", "PENDENTE", "null", "GERAL", "null")
                .replace("{", "{\"id\":\"" + idCliente + "\",\"origemTipo\":\"VENDA\",\"origemId\":\"" + idCliente + "\",");
        mvc.perform(post(URL).header("Authorization", "Bearer " + token).contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated());
        entityManager.flush();
        UUID id = jdbc.queryForObject("select id from transacao_financeira", UUID.class);
        assertThat(id).isNotEqualTo(idCliente);
        assertThat(jdbc.queryForObject("select origem_tipo from transacao_financeira", String.class)).isNull();
        assertThat(jdbc.queryForObject("select origem_id from transacao_financeira", UUID.class)).isNull();
    }

    @Test
    @DisplayName("@spec:AC-700 GET de lançamento ausente retorna erro 404 no contrato global")
    void consultaInexistente() throws Exception {
        mvc.perform(get(URL + "/{id}", UUID.randomUUID()).header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound()).andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.mensagem").value("Lançamento financeiro não encontrado."));
    }

    @Test
    @DisplayName("@spec:AC-701 GET com UUID inválido retorna 400")
    void consultaUuidInvalido() throws Exception {
        mvc.perform(get(URL + "/inválido").header("Authorization", "Bearer " + token))
                .andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    @ValueSource(strings = {"ausente", "invalido", "expirado"})
    @DisplayName("@spec:AC-700 Ambos os endpoints exigem JWT válido, sem persistência indevida")
    void exigeAutenticacao(String modo) throws Exception {
        var post = post(URL).contentType(MediaType.APPLICATION_JSON)
                .content(payload("DESPESA", "ENERGIA", "PENDENTE", "null", "GERAL", "null"));
        var get = get(URL + "/{id}", UUID.randomUUID());
        if (!modo.equals("ausente")) {
            String credencial = modo.equals("invalido") ? "inválido" : token(Instant.now().minusSeconds(60));
            post.header("Authorization", "Bearer " + credencial);
            get.header("Authorization", "Bearer " + credencial);
        }
        mvc.perform(post).andExpect(status().isUnauthorized());
        mvc.perform(get).andExpect(status().isUnauthorized());
        assertThat(jdbc.queryForObject("select count(*) from transacao_financeira", Long.class)).isZero();
    }

    private static String payload(String tipo, String categoria, String situacao, String efetivacao, String centro, String id) {
        return """
                {"tipo":"%s","descricao":"  Operação financeira  ","valor":150.50,"categoria":"%s",
                 "dataReferencia":"2025-01-10","dataVencimento":"2025-01-15","situacao":"%s",
                 "dataEfetivacao":%s,"tipoDeCusto":"%s","referenciaId":%s}
                """.formatted(tipo, categoria, situacao, efetivacao, centro, id);
    }

    private static String token(Instant expiracao) {
        return JWT.create().withIssuer("sistemajcb-auth").withSubject("dono").withExpiresAt(expiracao)
                .sign(Algorithm.HMAC256("test-secret"));
    }
}
