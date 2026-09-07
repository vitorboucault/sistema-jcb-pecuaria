package com.br.usecase.manejo;

import com.br.core.domain.enums.Categoria;
import com.br.core.domain.enums.Sexo;
import com.br.core.domain.enums.Status;
import com.br.core.domain.model.Animal;
import com.br.core.domain.repository.AnimalRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReverterMorteAnimalUseCaseTest {

    @Mock
    private AnimalRepository animalRepository;

    @InjectMocks
    private ReverterMorteAnimalUseCase useCase;

    @Test
    @DisplayName("Reverte morte, limpa data e mantém animal sem lote")
    void deveReverterMorte() {
        UUID animalId = UUID.randomUUID();
        Animal animal = animal(Status.MORTO, null, LocalDate.now().minusDays(1));
        when(animalRepository.buscarPorId(animalId)).thenReturn(Optional.of(animal));

        useCase.executar(animalId);

        assertThat(animal.getStatus()).isEqualTo(Status.ATIVO);
        assertThat(animal.getDataMorte()).isNull();
        assertThat(animal.getLoteId()).isNull();
        verify(animalRepository, times(1)).salvar(animal);
    }

    @Test
    void animalInexistenteEhRejeitadoESemSalvar() {
        UUID animalId = UUID.randomUUID();
        when(animalRepository.buscarPorId(animalId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.executar(animalId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Animal nao encontrado.");

        verify(animalRepository, never()).salvar(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void animalAtivoNaoPodeReverterMorteESemSalvar() {
        UUID animalId = UUID.randomUUID();
        Animal animal = animal(Status.ATIVO, null, null);
        when(animalRepository.buscarPorId(animalId)).thenReturn(Optional.of(animal));

        assertThatThrownBy(() -> useCase.executar(animalId))
                .isInstanceOf(IllegalStateException.class);

        verify(animalRepository, never()).salvar(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void animalVendidoNaoPodeReverterMorteESemSalvar() {
        UUID animalId = UUID.randomUUID();
        Animal animal = animal(Status.VENDIDO, null, null);
        when(animalRepository.buscarPorId(animalId)).thenReturn(Optional.of(animal));

        assertThatThrownBy(() -> useCase.executar(animalId))
                .isInstanceOf(IllegalStateException.class);

        verify(animalRepository, never()).salvar(org.mockito.ArgumentMatchers.any());
    }

    private Animal animal(Status status, UUID loteId, LocalDate dataMorte) {
        return new Animal(UUID.randomUUID(), "BRINCO-" + UUID.randomUUID(),
                LocalDate.now().minusYears(2), Sexo.MACHO, Categoria.BOI, status, null, loteId, dataMorte);
    }
}
