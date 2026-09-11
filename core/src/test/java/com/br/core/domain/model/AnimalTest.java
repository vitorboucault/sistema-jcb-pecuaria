package com.br.core.domain.model;

import com.br.core.domain.enums.Categoria;
import com.br.core.domain.enums.Sexo;
import com.br.core.domain.enums.Status;
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

    @Test
    @DisplayName("@spec:AC-201 Animal ativo recebe morte e preserva o lote")
    void animalAtivoPodeReceberMorte() {
        UUID loteId = UUID.randomUUID();
        Animal animal = animalComStatus(Status.ATIVO, loteId, null);

        animal.registrarMorte(LocalDate.now());

        assertThat(animal.getStatus()).isEqualTo(Status.MORTO);
        assertThat(animal.getLoteId()).isEqualTo(loteId);
        assertThat(animal.getDataMorte()).isEqualTo(LocalDate.now());
    }

    @Test
    void morteDeMortoOuVendidoEhBloqueada() {
        Animal morto = animalComStatus(Status.MORTO, null, LocalDate.now().minusDays(1));
        Animal vendido = animalComStatus(Status.VENDIDO, null, null);

        assertThatThrownBy(() -> morto.registrarMorte(LocalDate.now()))
                .isInstanceOf(IllegalStateException.class);
        assertThatThrownBy(() -> vendido.registrarMorte(LocalDate.now()))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("@spec:AC-202 Reverter morte reativa animal, limpa data e preserva lote")
    void reverterMorteReativaAnimalELimpaDataSemRecriarLote() {
        UUID loteId = UUID.randomUUID();
        Animal animal = animalComStatus(Status.MORTO, loteId, LocalDate.now().minusDays(1));

        animal.reverterMorte();

        assertThat(animal.getStatus()).isEqualTo(Status.ATIVO);
        assertThat(animal.getDataMorte()).isNull();
        assertThat(animal.getLoteId()).isEqualTo(loteId);
    }

    @Test
    void reverterMorteForaDeMortoEhBloqueada() {
        assertThatThrownBy(() -> animalComStatus(Status.ATIVO, null, null).reverterMorte())
                .isInstanceOf(IllegalStateException.class);
        assertThatThrownBy(() -> animalComStatus(Status.VENDIDO, null, null).reverterMorte())
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void animalAtivoPodeSerVendido() {
        Animal animal = animalComStatus(Status.ATIVO, UUID.randomUUID(), null);

        animal.registrarVenda();

        assertThat(animal.getStatus()).isEqualTo(Status.VENDIDO);
        assertThat(animal.getLoteId()).isNull();
    }

    @Test
    void vendaDeMortoOuVendidoEhBloqueada() {
        assertThatThrownBy(() -> animalComStatus(Status.MORTO, null, LocalDate.now()).registrarVenda())
                .isInstanceOf(IllegalStateException.class);
        assertThatThrownBy(() -> animalComStatus(Status.VENDIDO, null, null).registrarVenda())
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void reverterVendaReativaAnimalSemRecriarLote() {
        Animal animal = animalComStatus(Status.VENDIDO, UUID.randomUUID(), null);

        animal.reverterVenda();

        assertThat(animal.getStatus()).isEqualTo(Status.ATIVO);
        assertThat(animal.getLoteId()).isNull();
    }

    @Test
    void reverterVendaForaDeVendidoEhBloqueada() {
        assertThatThrownBy(() -> animalComStatus(Status.ATIVO, null, null).reverterVenda())
                .isInstanceOf(IllegalStateException.class);
        assertThatThrownBy(() -> animalComStatus(Status.MORTO, null, LocalDate.now()).reverterVenda())
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void movimentacaoDeMortoOuVendidoEhBloqueada() {
        assertThatThrownBy(() -> animalComStatus(Status.MORTO, null, LocalDate.now())
                .transferirParaLote(UUID.randomUUID()))
                .isInstanceOf(IllegalStateException.class);
        assertThatThrownBy(() -> animalComStatus(Status.VENDIDO, null, null)
                .transferirParaLote(UUID.randomUUID()))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void atualizacaoCadastralPreservaStatusDataMorteELote() {
        UUID loteId = UUID.randomUUID();
        LocalDate dataMorte = LocalDate.now().minusDays(1);
        Animal animal = animalComStatus(Status.MORTO, loteId, dataMorte);

        animal.atualizarDadosCadastrais("NOVO", LocalDate.now().minusYears(2), Sexo.FEMEA, Categoria.VACA);

        assertThat(animal.getStatus()).isEqualTo(Status.MORTO);
        assertThat(animal.getDataMorte()).isEqualTo(dataMorte);
        assertThat(animal.getLoteId()).isEqualTo(loteId);
    }

    @Test
    void atualizacaoCadastralComNascimentoFuturoEhBloqueada() {
        Animal animal = animalComStatus(Status.ATIVO, null, null);

        assertThatThrownBy(() -> animal.atualizarDadosCadastrais(
                "NOVO", LocalDate.now().plusDays(1), Sexo.MACHO, Categoria.BEZERRO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("nao pode nascer no futuro");
    }

    private Animal animalComStatus(Status status, UUID loteId, LocalDate dataMorte) {
        return new Animal(UUID.randomUUID(), "BRINCO-" + UUID.randomUUID(),
                LocalDate.now().minusYears(2), Sexo.MACHO, Categoria.BOI, status, null, loteId, dataMorte);
    }
}
