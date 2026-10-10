package com.br.infra.rebanho;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.br.core.domain.model.Pesagem;
import com.br.core.domain.model.AquisicaoAnimal;
import com.br.core.domain.repository.AquisicaoAnimalRepository;
import com.br.core.domain.repository.PesagemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class CadastroInicialAtomicidadeHttpTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private JdbcTemplate jdbc;

    @MockitoSpyBean
    private PesagemRepository pesagemRepository;

    @MockitoBean
    private AquisicaoAnimalRepository aquisicaoAnimalRepository;

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

    @Test
    @DisplayName("@spec:AC-338 Falha ao persistir pesagem desfaz o cadastro do animal")
    void falhaNaPesagemDesfazAnimal() throws Exception {
        String brinco = "ATOM-" + UUID.randomUUID();
        doThrow(new IllegalStateException("Falha simulada ao persistir pesagem"))
                .when(pesagemRepository).salvar(any(Pesagem.class));

        mvc.perform(post("/api/v1/animais/cadastrar")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"brincoRgd":"%s","dataNascimento":"2020-05-10","sexo":"FEMEA",
                                 "categoria":"VACA","origem":"DESCONHECIDO","pesoAtual":385.5,
                                 "dataPesagem":"2026-09-20"}
                                """.formatted(brinco)))
                .andExpect(status().isConflict());

        assertThat(jdbc.queryForObject("select count(*) from animal where brinco_rgd = ?", Long.class, brinco))
                .isZero();
    }

    @Test
    @DisplayName("@spec:AC-355 Falha ao persistir aquisição desfaz animal e pesagem")
    void falhaNaAquisicaoDesfazAnimalEPesagem() throws Exception {
        String brinco = "ATOM-AQUISICAO-" + UUID.randomUUID();
        doThrow(new IllegalStateException("Falha simulada ao persistir aquisição"))
                .when(aquisicaoAnimalRepository).salvar(any(AquisicaoAnimal.class));

        mvc.perform(post("/api/v1/animais/cadastrar")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"brincoRgd":"%s","dataNascimento":"2019-04-10","sexo":"FEMEA",
                                 "categoria":"VACA","origem":"COMPRA","pesoAtual":385.5,
                                 "dataPesagem":"2026-09-20","dataCompraHistorica":"2021-05-10",
                                 "valorCompraHistorico":3200.00}
                                """.formatted(brinco)))
                .andExpect(status().isConflict());

        assertThat(jdbc.queryForObject("select count(*) from animal where brinco_rgd = ?", Long.class, brinco))
                .isZero();
        assertThat(jdbc.queryForObject("select count(*) from pesagem p join animal a on a.id = p.animal_id where a.brinco_rgd = ?", Long.class, brinco))
                .isZero();
        assertThat(jdbc.queryForObject("select count(*) from aquisicao_animal a join animal on animal.id = a.animal_id where animal.brinco_rgd = ?", Long.class, brinco))
                .isZero();
    }
}
