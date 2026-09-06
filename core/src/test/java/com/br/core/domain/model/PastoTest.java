package com.br.core.domain.model;

import com.br.core.domain.enums.StatusPasto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PastoTest {

    @Test
    @DisplayName("Deve impedir entrada de animais em pasto sob manutenção/reforma")
    void deveImpedirEntradaEmPastoEmManutencao() {
        Pasto pasto = new Pasto(UUID.randomUUID(), "Pasto da Sede", 45.0, 60.0, StatusPasto.DESCANSO);
        pasto.iniciarManutencao();

        assertThat(pasto.getStatusAtual()).isEqualTo(StatusPasto.MANUTENCAO);

        assertThatThrownBy(pasto::registrarEntradaGado)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Nao é possível colocar gado em um pasto em manutençao/reforma.");
    }

    @Test
    @DisplayName("Deve permitir entrada e vedar pasto para rotação de capim")
    void deveAlternarStatusPastejoEDescanso() {
        Pasto pasto = new Pasto("Piquete 01", 30.0, 40.0);
        assertThat(pasto.getStatusAtual()).isEqualTo(StatusPasto.DESCANSO);

        pasto.registrarEntradaGado();
        assertThat(pasto.getStatusAtual()).isEqualTo(StatusPasto.PASTEJO);

        pasto.vedarPasto();
        assertThat(pasto.getStatusAtual()).isEqualTo(StatusPasto.DESCANSO);
    }

    @Test
    @DisplayName("Deve validar integridade das datas de movimentação de lote no pasto")
    void deveValidarDatasMovimentacaoLote() {
        UUID loteId = UUID.randomUUID();
        UUID pastoId = UUID.randomUUID();
        LocalDate entrada = LocalDate.now().minusDays(20);

        MovimentacaoLote mov = new MovimentacaoLote(UUID.randomUUID(), loteId, pastoId, entrada, null);
        assertThat(mov.isNoPasto()).isTrue();

        mov.registrarSaida(entrada.plusDays(10));
        assertThat(mov.isNoPasto()).isFalse();
        assertThat(mov.getDataSaida()).isEqualTo(entrada.plusDays(10));

        assertThatThrownBy(() -> mov.registrarSaida(entrada.minusDays(1)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Data de saída inválida.");
    }
}