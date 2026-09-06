package com.br.core.domain.model;

import com.br.core.domain.enums.ResultadoDiagnostico;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EstacaoMontaTest {

    @Test
    @DisplayName("Deve validar intervalo da estação e inclusão de datas de cobertura")
    void deveValidarPeriodoEstacaoMonta() {
        LocalDate inicio = LocalDate.of(2026, 11, 1);
        LocalDate fim = LocalDate.of(2027, 2, 28);
        EstacaoMonta estacao = new EstacaoMonta(UUID.randomUUID(), "Águas 26/27", inicio, fim, "ABERTA");

        assertThat(estacao.isAberta()).isTrue();
        assertThat(estacao.contemData(LocalDate.of(2026, 12, 15))).isTrue();
        assertThat(estacao.contemData(LocalDate.of(2026, 10, 31))).isFalse();
        assertThat(estacao.contemData(LocalDate.of(2027, 3, 1))).isFalse();

        estacao.fecharEstacao();
        assertThat(estacao.isAberta()).isFalse();
        assertThat(estacao.getStatus()).isEqualTo("FECHADA");
    }

    @Test
    @DisplayName("Não deve permitir data inicial posterior à data final da estação")
    void deveBarrarDataInicialMaiorQueFinal() {
        LocalDate inicio = LocalDate.of(2027, 3, 1);
        LocalDate fim = LocalDate.of(2026, 11, 1);

        assertThatThrownBy(() -> new EstacaoMonta(UUID.randomUUID(), "Estação Inválida", inicio, fim, "ABERTA"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("A data de início nao pode ser depois da data de fim da Estaçao.");
    }

    @Test
    @DisplayName("Deve projetar data provável de parto adicionando 295 dias apenas para diagnóstico PRENHE")
    void deveProjetarPartoParaMatrizPrenhe() {
        LocalDate dataDiagnostico = LocalDate.of(2026, 12, 1);
        LocalDate dataInseminacao = LocalDate.of(2026, 11, 1);

        DiagnosticoGestacao diagPrenhe = new DiagnosticoGestacao(
                UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), dataDiagnostico, ResultadoDiagnostico.PRENHE
        );
        diagPrenhe.projetarParto(dataInseminacao);

        assertThat(diagPrenhe.getDataProvavelParto()).isEqualTo(dataInseminacao.plusDays(295));

        DiagnosticoGestacao diagVazia = new DiagnosticoGestacao(
                UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), dataDiagnostico, ResultadoDiagnostico.VAZIA
        );
        diagVazia.projetarParto(dataInseminacao);

        assertThat(diagVazia.getDataProvavelParto()).isNull();
    }
}