package com.br.core.domain.model;

import com.br.core.domain.enums.OrigemPesagem;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PesagemTest {

    @Test
    void origemNulaNoConstrutorDeNovaPesagemEhBloqueada() {
        assertThatThrownBy(() -> new Pesagem(UUID.randomUUID(), LocalDate.now(), 100.0, false, null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void origemNulaNoConstrutorDePesagemExistenteEhBloqueada() {
        assertThatThrownBy(() -> new Pesagem(UUID.randomUUID(), UUID.randomUUID(), LocalDate.now(), 100.0, false, null))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
