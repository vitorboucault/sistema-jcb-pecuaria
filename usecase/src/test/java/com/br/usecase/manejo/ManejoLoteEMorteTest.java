package com.br.usecase.manejo;

import com.br.core.domain.enums.Categoria;
import com.br.core.domain.enums.FaseLote;
import com.br.core.domain.enums.Sexo;
import com.br.core.domain.enums.Status;
import com.br.core.domain.model.Animal;
import com.br.core.domain.model.Lote;
import com.br.core.domain.repository.AnimalRepository;
import com.br.core.domain.repository.LoteRepository;
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
    @DisplayName("Deve registrar baixa sanitária por morte, marcando MORTO e desvinculando do lote")
    void deveRegistrarBaixaMorte() {
        UUID animalId = UUID.randomUUID();
        UUID loteId = UUID.randomUUID();
        Animal animal = new Animal(animalId, "VACA-01", LocalDate.now().minusYears(3), Sexo.FEMEA, Categoria.VACA, Status.ATIVO, null, loteId);

        when(animalRepository.buscarPorId(animalId)).thenReturn(Optional.of(animal));

        registrarMorteUseCase.executar(animalId);

        assertThat(animal.getStatus()).isEqualTo(Status.MORTO);
        assertThat(animal.getLoteId()).isNull();
        verify(animalRepository, times(1)).salvar(animal);
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