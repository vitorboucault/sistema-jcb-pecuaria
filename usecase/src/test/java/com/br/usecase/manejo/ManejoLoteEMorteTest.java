package com.br.usecase.manejo;

import com.br.core.domain.enums.Categoria;
import com.br.core.domain.enums.FaseLote;
import com.br.core.domain.enums.Sexo;
import com.br.core.domain.enums.Status;
import com.br.core.domain.model.Animal;
import com.br.core.domain.model.Lote;
import com.br.core.domain.repository.AnimalRepository;
import com.br.core.domain.repository.LoteRepository;
import com.br.usecase.dto.RegistrarMorteAnimalCommand;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ManejoLoteEMorteTest {

    @Mock
    private AnimalRepository animalRepository;

    @Mock
    private LoteRepository loteRepository;

    @InjectMocks
    private RegistrarMorteAnimalUseCase registrarMorteUseCase;

    @InjectMocks
    private EncerrarLoteUseCase encerrarLoteUseCase;

    @Test
    @DisplayName("Animal ativo recebe morte e passa para MORTO")
    void animalAtivoRecebeMorteEPassaParaMorto() {
        UUID animalId = UUID.randomUUID();
        UUID loteId = UUID.randomUUID();
        LocalDate dataMorte = LocalDate.now();
        Animal animal = new Animal(animalId, "VACA-01", LocalDate.now().minusYears(3), Sexo.FEMEA, Categoria.VACA, Status.ATIVO, null, loteId);

        when(animalRepository.buscarPorId(animalId)).thenReturn(Optional.of(animal));

        registrarMorteUseCase.executar(new RegistrarMorteAnimalCommand(animalId, dataMorte));

        assertThat(animal.getStatus()).isEqualTo(Status.MORTO);
        verify(animalRepository, times(1)).salvar(animal);
    }

    @Test
    @DisplayName("Data de morte deve ser persistida")
    void dataMorteDeveSerPersistida() {
        UUID animalId = UUID.randomUUID();
        LocalDate dataMorte = LocalDate.now().minusDays(2);
        Animal animal = new Animal(animalId, "VACA-02", LocalDate.now().minusYears(3), Sexo.FEMEA, Categoria.VACA, Status.ATIVO, null, UUID.randomUUID());

        when(animalRepository.buscarPorId(animalId)).thenReturn(Optional.of(animal));

        registrarMorteUseCase.executar(new RegistrarMorteAnimalCommand(animalId, dataMorte));

        ArgumentCaptor<Animal> captor = ArgumentCaptor.forClass(Animal.class);
        verify(animalRepository).salvar(captor.capture());
        assertThat(captor.getValue().getDataMorte()).isEqualTo(dataMorte);
    }

    @Test
    @DisplayName("@spec:AC-201 Lote é preservado ao registrar morte")
    void loteDeveSerPreservadoAoRegistrarMorte() {
        UUID animalId = UUID.randomUUID();
        UUID loteId = UUID.randomUUID();
        Animal animal = new Animal(animalId, "VACA-03", LocalDate.now().minusYears(3), Sexo.FEMEA, Categoria.VACA, Status.ATIVO, null, loteId);

        when(animalRepository.buscarPorId(animalId)).thenReturn(Optional.of(animal));

        registrarMorteUseCase.executar(new RegistrarMorteAnimalCommand(animalId, LocalDate.now()));

        assertThat(animal.getLoteId()).isEqualTo(loteId);
        verify(animalRepository).salvar(animal);
    }

    @Test
    @DisplayName("Animal inexistente deve gerar erro ao registrar morte")
    void animalInexistenteDeveGerarErroAoRegistrarMorte() {
        UUID animalId = UUID.randomUUID();

        when(animalRepository.buscarPorId(animalId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> registrarMorteUseCase.executar(new RegistrarMorteAnimalCommand(animalId, LocalDate.now())))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Animal nao encontrado.");

        verify(animalRepository, never()).salvar(any());
    }

    @Test
    @DisplayName("Animal ja morto deve gerar erro ao registrar morte")
    void animalJaMortoDeveGerarErroAoRegistrarMorte() {
        UUID animalId = UUID.randomUUID();
        Animal animal = new Animal(animalId, "VACA-04", LocalDate.now().minusYears(3), Sexo.FEMEA, Categoria.VACA, Status.MORTO, null, null);

        when(animalRepository.buscarPorId(animalId)).thenReturn(Optional.of(animal));

        assertThatThrownBy(() -> registrarMorteUseCase.executar(new RegistrarMorteAnimalCommand(animalId, LocalDate.now())))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Somente animais ativos podem receber baixa por morte.");

        verify(animalRepository, never()).salvar(any());
    }

    @Test
    @DisplayName("Data futura deve gerar erro ao registrar morte")
    void dataFuturaDeveGerarErroAoRegistrarMorte() {
        UUID animalId = UUID.randomUUID();
        Animal animal = new Animal(animalId, "VACA-05", LocalDate.now().minusYears(3), Sexo.FEMEA, Categoria.VACA, Status.ATIVO, null, UUID.randomUUID());

        when(animalRepository.buscarPorId(animalId)).thenReturn(Optional.of(animal));

        assertThatThrownBy(() -> registrarMorteUseCase.executar(new RegistrarMorteAnimalCommand(animalId, LocalDate.now().plusDays(1))))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("A data da morte nao pode ser futura.");

        verify(animalRepository, never()).salvar(any());
    }

    @Test
    @DisplayName("Data de morte obrigatoria deve gerar erro ao registrar morte")
    void dataMorteObrigatoriaDeveGerarErroAoRegistrarMorte() {
        UUID animalId = UUID.randomUUID();
        Animal animal = new Animal(animalId, "VACA-06", LocalDate.now().minusYears(3), Sexo.FEMEA, Categoria.VACA, Status.ATIVO, null, UUID.randomUUID());

        when(animalRepository.buscarPorId(animalId)).thenReturn(Optional.of(animal));

        assertThatThrownBy(() -> registrarMorteUseCase.executar(new RegistrarMorteAnimalCommand(animalId, null)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("A data da morte é obrigatória.");

        verify(animalRepository, never()).salvar(any());
    }

    @Test
    @DisplayName("Deve encerrar lote em andamento preenchendo a data de encerramento")
    void deveEncerrarLote() {
        UUID loteId = UUID.randomUUID();
        Lote lote = new Lote(loteId, "LOTE-RECRIA-2026", FaseLote.RECRIA, LocalDate.now().minusDays(180), null);

        when(loteRepository.buscarPorId(loteId)).thenReturn(Optional.of(lote));

        encerrarLoteUseCase.executar(loteId);

        assertThat(lote.isAtivo()).isFalse();
        assertThat(lote.getDataEncerramento()).isEqualTo(LocalDate.now());
        verify(loteRepository, times(1)).salvar(lote);
    }
}
