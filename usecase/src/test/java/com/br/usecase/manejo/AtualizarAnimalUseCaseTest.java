package com.br.usecase.manejo;

import com.br.core.domain.enums.Categoria;
import com.br.core.domain.enums.Sexo;
import com.br.core.domain.enums.Status;
import com.br.core.domain.model.Animal;
import com.br.core.domain.repository.AnimalRepository;
import com.br.usecase.dto.AtualizarAnimalCommand;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AtualizarAnimalUseCaseTest {

    @Mock
    private AnimalRepository animalRepository;

    @InjectMocks
    private AtualizarAnimalUseCase useCase;

    @Test
    @DisplayName("Atualiza animal existente")
    void deveAtualizarAnimalExistente() {
        UUID animalId = UUID.randomUUID();
        Animal animal = new Animal(animalId, "ANTIGO", LocalDate.now().minusYears(2), Sexo.MACHO, Categoria.GARROTE, Status.ATIVO, null, UUID.randomUUID());
        AtualizarAnimalCommand command = new AtualizarAnimalCommand(
                animalId,
                "NOVO",
                LocalDate.now().minusYears(3),
                Sexo.FEMEA,
                Categoria.VACA
        );

        when(animalRepository.buscarPorId(animalId)).thenReturn(Optional.of(animal));
        when(animalRepository.buscarPorBrinco("NOVO")).thenReturn(Optional.empty());

        useCase.executar(command);

        ArgumentCaptor<Animal> captor = ArgumentCaptor.forClass(Animal.class);
        verify(animalRepository).salvar(captor.capture());
        Animal salvo = captor.getValue();

        assertThat(salvo.getBrincoRgd()).isEqualTo("NOVO");
        assertThat(salvo.getDataNascimento()).isEqualTo(command.dataNascimento());
        assertThat(salvo.getSexo()).isEqualTo(Sexo.FEMEA);
        assertThat(salvo.getCategoriaAtual()).isEqualTo(Categoria.VACA);
    }

    @Test
    @DisplayName("Animal inexistente gera erro ao atualizar")
    void animalInexistenteGeraErroAoAtualizar() {
        UUID animalId = UUID.randomUUID();
        AtualizarAnimalCommand command = new AtualizarAnimalCommand(
                animalId,
                "NOVO",
                LocalDate.now().minusYears(3),
                Sexo.FEMEA,
                Categoria.VACA
        );

        when(animalRepository.buscarPorId(animalId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.executar(command))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Animal nao encontrado.");

        verify(animalRepository, never()).salvar(any());
    }

    @Test
    @DisplayName("Brinco duplicado gera erro ao atualizar")
    void brincoDuplicadoGeraErroAoAtualizar() {
        UUID animalId = UUID.randomUUID();
        Animal animal = new Animal(animalId, "ANTIGO", LocalDate.now().minusYears(2), Sexo.MACHO, Categoria.GARROTE, Status.ATIVO, null, null);
        Animal outro = new Animal(UUID.randomUUID(), "NOVO", LocalDate.now().minusYears(2), Sexo.MACHO, Categoria.BOI, Status.ATIVO, null, null);
        AtualizarAnimalCommand command = new AtualizarAnimalCommand(
                animalId,
                "NOVO",
                LocalDate.now().minusYears(3),
                Sexo.FEMEA,
                Categoria.VACA
        );

        when(animalRepository.buscarPorId(animalId)).thenReturn(Optional.of(animal));
        when(animalRepository.buscarPorBrinco("NOVO")).thenReturn(Optional.of(outro));

        assertThatThrownBy(() -> useCase.executar(command))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Erro: Já existe um animal com o brinco");

        verify(animalRepository, never()).salvar(any());
    }

    @Test
    @DisplayName("Atualizacao nao altera status nem historico")
    void atualizacaoNaoAlteraStatusNemHistorico() {
        UUID animalId = UUID.randomUUID();
        UUID loteId = UUID.randomUUID();
        LocalDate dataMorte = LocalDate.now().minusDays(1);
        Animal animal = new Animal(animalId, "ANTIGO", LocalDate.now().minusYears(2), Sexo.MACHO, Categoria.GARROTE, Status.MORTO, null, loteId, dataMorte);
        AtualizarAnimalCommand command = new AtualizarAnimalCommand(
                animalId,
                "NOVO",
                LocalDate.now().minusYears(3),
                Sexo.FEMEA,
                Categoria.VACA
        );

        when(animalRepository.buscarPorId(animalId)).thenReturn(Optional.of(animal));
        when(animalRepository.buscarPorBrinco("NOVO")).thenReturn(Optional.empty());

        useCase.executar(command);

        assertThat(animal.getStatus()).isEqualTo(Status.MORTO);
        assertThat(animal.getDataMorte()).isEqualTo(dataMorte);
        assertThat(animal.getLoteId()).isEqualTo(loteId);
    }
}
