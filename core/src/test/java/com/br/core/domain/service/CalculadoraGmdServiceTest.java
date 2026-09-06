package com.br.core.domain.service;

import com.br.core.domain.model.Pesagem;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class CalculadoraGmdServiceTest {
    private final CalculadoraGmdService calculadora = new CalculadoraGmdService();
    private final UUID animalId = UUID.randomUUID();

    @Test
    @DisplayName("Deve calcular o GMD corretamente para um período de 100 dias")
    void deveCalcularGmdCorretamente() {
        Pesagem pesagemAnterior = new Pesagem(animalId, LocalDate.now().minusDays(100), 200.0, false);
        Pesagem pesagemAtual = new Pesagem(animalId, LocalDate.now(), 280.0, false);

        double gmd = calculadora.calcularGmd(pesagemAnterior, pesagemAtual);

        assertThat(gmd).isEqualTo(0.800);
    }

    @Test
    @DisplayName("Deve barrar pesagem atual com data retroativa")
    void deveLancarExcecaoSeDataInvertida() {
        Pesagem pesagemAnterior = new Pesagem(animalId, LocalDate.now(), 200.0, false);
        Pesagem pesagemAtual = new Pesagem(animalId, LocalDate.now().minusDays(10), 210.0, false);

        assertThatThrownBy(() -> calculadora.calcularGmd(pesagemAnterior, pesagemAtual))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("A pesagem atual nao pode ser anterior");
    }
}
