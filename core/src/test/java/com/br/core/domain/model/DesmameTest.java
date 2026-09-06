package com.br.core.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;

class DesmameTest {

    @Test
    @DisplayName("Deve calcular o peso ajustado aos 205 dias corretamente")
    void deveCalcularPesoAjustado205Dias() {
        // Cenário: Bezerro nasceu com 30kg e foi desmamado com 240kg aos 210 dias de vida.
        // GMD = (240 - 30) / 210 = 1 kg/dia.
        // Peso Ajustado 205d = 30kg + (1kg * 205 dias) = 235 kg.
        LocalDate dataNascimento = LocalDate.now().minusDays(210);
        LocalDate dataDesmame = LocalDate.now();

        Desmame desmame = new Desmame(
                UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
                dataNascimento, dataDesmame, 30.0, 240.0
        );

        Double pesoAjustado = desmame.calcularPesoAjustado210Dias();

        assertThat(pesoAjustado).isEqualTo(240.0);
    }
}