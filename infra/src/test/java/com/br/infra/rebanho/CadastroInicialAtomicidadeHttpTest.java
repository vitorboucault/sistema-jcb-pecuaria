package com.br.infra.rebanho;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.br.core.domain.model.Pesagem;
import com.br.core.domain.repository.PesagemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
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

    @MockitoBean
    private PesagemRepository pesagemRepository;

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
}
