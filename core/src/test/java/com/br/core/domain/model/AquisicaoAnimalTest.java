package com.br.core.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AquisicaoAnimalTest {

    @Test
    @DisplayName("@spec:AC-350 AquisicaoAnimal preserva animal, data e valor")
    void preservaDadosInformados() {
        UUID id = UUID.randomUUID();
        UUID animalId = UUID.randomUUID();
        LocalDate data = LocalDate.of(2021, 5, 10);
        BigDecimal valor = new BigDecimal("3200.00");

        AquisicaoAnimal aquisicao = new AquisicaoAnimal(
                id, animalId, data, valor, LocalDate.of(2019, 4, 10)
        );

        assertThat(aquisicao.getId()).isEqualTo(id);
        assertThat(aquisicao.getAnimalId()).isEqualTo(animalId);
        assertThat(aquisicao.getDataAquisicao()).isEqualTo(data);
        assertThat(aquisicao.getValorAquisicao()).isEqualByComparingTo(valor);
    }

    @Test
    @DisplayName("@spec:AC-351 AquisicaoAnimal aceita data e valor independentemente")
    void aceitaCamposOpcionaisIndependentemente() {
        AquisicaoAnimal somenteData = new AquisicaoAnimal(
                UUID.randomUUID(), UUID.randomUUID(), LocalDate.of(2021, 5, 10), null
        );
        AquisicaoAnimal somenteValor = new AquisicaoAnimal(
                UUID.randomUUID(), UUID.randomUUID(), null, new BigDecimal("3200.00")
        );

        assertThat(somenteData.getValorAquisicao()).isNull();
        assertThat(somenteValor.getDataAquisicao()).isNull();
    }

    @Test
    @DisplayName("@spec:AC-353 AquisicaoAnimal rejeita valor não positivo")
    void rejeitaValorNaoPositivo() {
        assertThatThrownBy(() -> new AquisicaoAnimal(
                UUID.randomUUID(), UUID.randomUUID(), null, BigDecimal.ZERO
        )).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("O valor da aquisição deve ser maior que zero.");

        assertThatThrownBy(() -> new AquisicaoAnimal(
                UUID.randomUUID(), UUID.randomUUID(), null, new BigDecimal("-1.00")
        )).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("O valor da aquisição deve ser maior que zero.");
    }

    @Test
    @DisplayName("@spec:AC-353 AquisicaoAnimal rejeita data futura ou anterior ao nascimento")
    void rejeitaDatasInvalidas() {
        LocalDate nascimento = LocalDate.of(2019, 4, 10);

        assertThatThrownBy(() -> new AquisicaoAnimal(
                UUID.randomUUID(), UUID.randomUUID(), LocalDate.now().plusDays(1), null, nascimento
        )).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("A data da aquisição não pode ser futura.");

        assertThatThrownBy(() -> new AquisicaoAnimal(
                UUID.randomUUID(), UUID.randomUUID(), nascimento.minusDays(1), null, nascimento
        )).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("A data da aquisição não pode ser anterior à data de nascimento.");
    }

    @Test
    @DisplayName("@spec:AC-349 AquisicaoAnimal não representa registro completamente vazio")
    void rejeitaRegistroSemDadosConhecidos() {
        assertThatThrownBy(() -> new AquisicaoAnimal(
                UUID.randomUUID(), UUID.randomUUID(), null, null
        )).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("A aquisição deve possuir data ou valor conhecido.");
    }
}
