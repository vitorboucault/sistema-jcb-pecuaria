package com.br.core.domain.model;

import com.br.core.domain.enums.ModalidadeVenda;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class VendaAnimalTest {

    @Test
    @DisplayName("Deve calcular receita no frigorífico com rendimento de carcaça e arroba a R$ 300,00")
    void deveCalcularVendaFrigorifico() {
        // Boi de 500 kg vivo com 54% de rendimento de carcaça = 270 kg carcaça
        // 270 kg / 15 = 18 arrobas
        // 18 arrobas * R$ 300,00 = R$ 5.400,00
        VendaAnimal venda = new VendaAnimal(
                UUID.randomUUID(), UUID.randomUUID(), LocalDate.now(),
                ModalidadeVenda.FRIGORIFICO, 500.0, 54.0, new BigDecimal("300.00")
        );

        BigDecimal receita = venda.calcularReceitaTotal();

        assertThat(receita).isEqualByComparingTo("5400.00");
    }

    @Test
    @DisplayName("Deve barrar venda de frigorífico se rendimento de carcaça não for informado")
    void deveExigirRendimentoDeCarcacaNoFrigorifico() {
        VendaAnimal venda = new VendaAnimal(
                UUID.randomUUID(), UUID.randomUUID(), LocalDate.now(),
                ModalidadeVenda.FRIGORIFICO, 500.0, null, new BigDecimal("300.00")
        );

        assertThatThrownBy(venda::calcularReceitaTotal)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Venda para frigorífico exige rendimento de carcaça.");
    }

    @Test
    @DisplayName("Deve calcular receita por peso vivo na fazenda (peso vivo / 30)")
    void deveCalcularVendaPorPesoVivo() {
        // Garrote de 360 kg vivo vendido a peso vivo por R$ 320,00/@
        // 360 / 30 = 12 arrobas
        // 12 arrobas * R$ 320,00 = R$ 3.840,00
        VendaAnimal venda = new VendaAnimal(
                UUID.randomUUID(), UUID.randomUUID(), LocalDate.now(),
                ModalidadeVenda.PESO, 360.0, null, new BigDecimal("320.00")
        );

        BigDecimal receita = venda.calcularReceitaTotal();

        assertThat(receita).isEqualByComparingTo("3840.00");
    }

    @Test
    @DisplayName("Deve calcular receita de venda por cabeça fixa")
    void deveCalcularVendaPorCabeca() {
        VendaAnimal venda = new VendaAnimal(
                UUID.randomUUID(), UUID.randomUUID(), LocalDate.now(),
                ModalidadeVenda.POR_CABECA, 200.0, null, new BigDecimal("2500.00")
        );

        BigDecimal receita = venda.calcularReceitaTotal();

        assertThat(receita).isEqualByComparingTo("2500.00");
    }
}