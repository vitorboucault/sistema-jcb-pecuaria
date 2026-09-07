package com.br.usecase.manejo;

import com.br.core.domain.enums.Categoria;
import com.br.core.domain.enums.Sexo;
import com.br.core.domain.enums.Status;
import com.br.core.domain.model.Animal;
import com.br.core.domain.repository.AnimalRepository;
import com.br.core.domain.repository.DespesaRepository;
import com.br.core.domain.repository.DiagnosticoGestacaoRepository;
import com.br.core.domain.repository.EventoReprodutivoRepository;
import com.br.core.domain.repository.PesagemRepository;
import com.br.core.domain.repository.VendaAnimalRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExcluirAnimalUseCaseTest {

    @Mock
    private AnimalRepository animalRepository;

    @Mock
    private PesagemRepository pesagemRepository;

    @Mock
    private DespesaRepository despesaRepository;

    @Mock
    private VendaAnimalRepository vendaAnimalRepository;

    @Mock
    private EventoReprodutivoRepository eventoReprodutivoRepository;

    @Mock
    private DiagnosticoGestacaoRepository diagnosticoGestacaoRepository;

    @InjectMocks
    private ExcluirAnimalUseCase useCase;

    @Test
    @DisplayName("Exclui animal sem historico")
    void deveExcluirAnimalSemHistorico() {
        UUID animalId = UUID.randomUUID();
        Animal animal = new Animal(animalId, "ENGANO-01", LocalDate.now().minusYears(1), Sexo.MACHO, Categoria.BEZERRO, Status.ATIVO, null, null);

        when(animalRepository.buscarPorId(animalId)).thenReturn(Optional.of(animal));

        useCase.executar(animalId);

        verify(animalRepository).excluirPorId(animalId);
    }

    @Test
    @DisplayName("Animal inexistente gera erro ao excluir")
    void animalInexistenteGeraErroAoExcluir() {
        UUID animalId = UUID.randomUUID();

        when(animalRepository.buscarPorId(animalId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.executar(animalId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Animal nao encontrado.");

        verify(animalRepository, never()).excluirPorId(animalId);
    }

    @Test
    @DisplayName("Animal com dependencias nao pode ser excluido")
    void animalComDependenciasNaoPodeSerExcluido() {
        UUID animalId = UUID.randomUUID();
        Animal animal = new Animal(animalId, "COM-HISTORICO", LocalDate.now().minusYears(1), Sexo.MACHO, Categoria.BEZERRO, Status.ATIVO, null, null);

        when(animalRepository.buscarPorId(animalId)).thenReturn(Optional.of(animal));
        when(pesagemRepository.existePorAnimalId(animalId)).thenReturn(true);

        assertThatThrownBy(() -> useCase.executar(animalId))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Animal possui historico ou vinculos e nao pode ser excluido.");

        verify(animalRepository, never()).excluirPorId(animalId);
    }

    @Test
    @DisplayName("Animal morto nao pode ser excluido")
    void animalMortoNaoPodeSerExcluido() {
        UUID animalId = UUID.randomUUID();
        Animal animal = new Animal(animalId, "MORTO-01", LocalDate.now().minusYears(1), Sexo.MACHO, Categoria.BEZERRO, Status.MORTO, null, null, LocalDate.now());

        when(animalRepository.buscarPorId(animalId)).thenReturn(Optional.of(animal));

        assertThatThrownBy(() -> useCase.executar(animalId))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Animal morto nao pode ser excluido pela interface normal.");

        verify(animalRepository, never()).excluirPorId(animalId);
    }

    @Test
    @DisplayName("Exclusao nao deve apagar historico relacionado por cascade acidental")
    void exclusaoNaoDeveApagarHistoricoRelacionadoPorCascadeAcidental() {
        UUID animalId = UUID.randomUUID();
        Animal animal = new Animal(animalId, "COM-DESPESA", LocalDate.now().minusYears(1), Sexo.MACHO, Categoria.BEZERRO, Status.ATIVO, null, null);

        when(animalRepository.buscarPorId(animalId)).thenReturn(Optional.of(animal));
        when(despesaRepository.existePorAnimalId(animalId)).thenReturn(true);

        assertThatThrownBy(() -> useCase.executar(animalId))
                .isInstanceOf(IllegalStateException.class);

        verify(animalRepository, never()).excluirPorId(animalId);
    }
}
