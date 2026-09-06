package com.br.core.domain.model;

import com.br.core.domain.enums.Categoria;
import com.br.core.domain.enums.Sexo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class AnimalTest {
    @Test
    @DisplayName("Deve desmamar um bezerro macho para garrote com mais de 5 meses")
    void deveDesmamarBezerroMacho() {
        Animal bezerro = new Animal("1234", LocalDate.now().minusMonths(9), Sexo.MACHO, null, UUID.randomUUID());

        bezerro.registrarDesmame();

        assertThat(bezerro.getCategoriaAtual()).isEqualTo(Categoria.GARROTE);
    }

    @Test
    @DisplayName("Não deve permitir desmame precoce (menos de 5 meses)")
    void naoDeveDesmamarPrecoce() {
        Animal bezerro = new Animal("5678", LocalDate.now().minusMonths(3), Sexo.FEMEA, null, UUID.randomUUID());

        assertThatThrownBy(bezerro::registrarDesmame)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Animal muito jovem para desmame precoce (Minimo 8 meses).");
    }
}
