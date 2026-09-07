package com.br.usecase.manejo;

import com.br.core.domain.enums.Categoria;
import com.br.core.domain.enums.CategoriaDespesa;
import com.br.core.domain.enums.OrigemPesagem;
import com.br.core.domain.enums.Sexo;
import com.br.core.domain.enums.Status;
import com.br.core.domain.enums.TipoDeCusto;
import com.br.core.domain.model.Animal;
import com.br.core.domain.model.Despesa;
import com.br.core.domain.model.Pesagem;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
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
    @DisplayName("Animal com pesagem inicial pode ser excluido")
    void animalComPesagemInicialPodeSerExcluido() {
        UUID animalId = UUID.randomUUID();
        Pesagem pesagemInicial = new Pesagem(UUID.randomUUID(), animalId, LocalDate.now().minusDays(10), 180.0, false, OrigemPesagem.CADASTRO_INICIAL);
        Animal animal = new Animal(animalId, "PESO-INICIAL", LocalDate.now().minusYears(1), Sexo.MACHO, Categoria.BEZERRO, Status.ATIVO, null, null);

        when(animalRepository.buscarPorId(animalId)).thenReturn(Optional.of(animal));
        when(pesagemRepository.buscarHistoricoPorAnimal(animalId)).thenReturn(List.of(pesagemInicial));
        when(despesaRepository.buscarPorAnimal(animalId)).thenReturn(List.of());

        useCase.executar(animalId);

        verify(pesagemRepository).excluirPorId(pesagemInicial.getId());
        verify(animalRepository).excluirPorId(animalId);
    }

    @Test
    @DisplayName("Animal comprado com despesa inicial pode ser excluido")
    void animalCompradoComDespesaInicialPodeSerExcluido() {
        UUID animalId = UUID.randomUUID();
        Despesa despesaCompra = new Despesa(
                UUID.randomUUID(),
                "Compra do animal COMPRA-01",
                new BigDecimal("2500.00"),
                LocalDate.now().minusDays(10),
                CategoriaDespesa.COMPRA_ANIMAL,
                TipoDeCusto.ANIMAL,
                animalId
        );
        Animal animal = new Animal(animalId, "COMPRA-01", LocalDate.now().minusYears(1), Sexo.MACHO, Categoria.BEZERRO, Status.ATIVO, null, null);

        when(animalRepository.buscarPorId(animalId)).thenReturn(Optional.of(animal));
        when(pesagemRepository.buscarHistoricoPorAnimal(animalId)).thenReturn(List.of());
        when(despesaRepository.buscarPorAnimal(animalId)).thenReturn(List.of(despesaCompra));

        useCase.executar(animalId);

        verify(despesaRepository).excluirPorId(despesaCompra.getId());
        verify(animalRepository).excluirPorId(animalId);
    }

    @Test
    @DisplayName("Pesagem posterior bloqueia exclusao")
    void pesagemPosteriorBloqueiaExclusao() {
        UUID animalId = UUID.randomUUID();
        Animal animal = new Animal(animalId, "COM-HISTORICO", LocalDate.now().minusYears(1), Sexo.MACHO, Categoria.BEZERRO, Status.ATIVO, null, null);
        Pesagem pesagemInicial = new Pesagem(UUID.randomUUID(), animalId, LocalDate.now().minusMonths(2), 180.0, false, OrigemPesagem.CADASTRO_INICIAL);
        Pesagem pesagemPosterior = new Pesagem(UUID.randomUUID(), animalId, LocalDate.now().minusMonths(1), 210.0, false, OrigemPesagem.OPERACIONAL);

        when(animalRepository.buscarPorId(animalId)).thenReturn(Optional.of(animal));
        when(pesagemRepository.buscarHistoricoPorAnimal(animalId)).thenReturn(List.of(pesagemInicial, pesagemPosterior));

        assertThatThrownBy(() -> useCase.executar(animalId))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Animal possui historico ou vinculos e nao pode ser excluido.");

        verify(animalRepository, never()).excluirPorId(animalId);
        verify(pesagemRepository, never()).excluirPorId(pesagemInicial.getId());
        verify(pesagemRepository, never()).excluirPorId(pesagemPosterior.getId());
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
    @DisplayName("Venda, reproducao ou morte bloqueiam exclusao")
    void vendaReproducaoOuMorteBloqueiamExclusao() {
        UUID animalComVendaId = UUID.randomUUID();
        Animal animalComVenda = new Animal(animalComVendaId, "COM-VENDA", LocalDate.now().minusYears(1), Sexo.MACHO, Categoria.BEZERRO, Status.ATIVO, null, null);

        when(animalRepository.buscarPorId(animalComVendaId)).thenReturn(Optional.of(animalComVenda));
        when(pesagemRepository.buscarHistoricoPorAnimal(animalComVendaId)).thenReturn(List.of());
        when(despesaRepository.buscarPorAnimal(animalComVendaId)).thenReturn(List.of());
        when(vendaAnimalRepository.existePorAnimalId(animalComVendaId)).thenReturn(true);

        assertThatThrownBy(() -> useCase.executar(animalComVendaId))
                .isInstanceOf(IllegalStateException.class);

        UUID animalComReproducaoId = UUID.randomUUID();
        Animal animalComReproducao = new Animal(animalComReproducaoId, "COM-REPRO", LocalDate.now().minusYears(1), Sexo.FEMEA, Categoria.VACA, Status.ATIVO, null, null);

        when(animalRepository.buscarPorId(animalComReproducaoId)).thenReturn(Optional.of(animalComReproducao));
        when(pesagemRepository.buscarHistoricoPorAnimal(animalComReproducaoId)).thenReturn(List.of());
        when(despesaRepository.buscarPorAnimal(animalComReproducaoId)).thenReturn(List.of());
        when(eventoReprodutivoRepository.existePorAnimalId(animalComReproducaoId)).thenReturn(true);

        assertThatThrownBy(() -> useCase.executar(animalComReproducaoId))
                .isInstanceOf(IllegalStateException.class);

        UUID animalMortoId = UUID.randomUUID();
        Animal animalMorto = new Animal(animalMortoId, "MORTO-02", LocalDate.now().minusYears(1), Sexo.MACHO, Categoria.BEZERRO, Status.MORTO, null, null, LocalDate.now());

        when(animalRepository.buscarPorId(animalMortoId)).thenReturn(Optional.of(animalMorto));

        assertThatThrownBy(() -> useCase.executar(animalMortoId))
                .isInstanceOf(IllegalStateException.class);

        verify(animalRepository, never()).excluirPorId(animalComVendaId);
        verify(animalRepository, never()).excluirPorId(animalComReproducaoId);
        verify(animalRepository, never()).excluirPorId(animalMortoId);
    }

    @Test
    @DisplayName("Exclusao remove somente registros iniciais relacionados")
    void exclusaoRemoveSomenteRegistrosIniciaisRelacionados() {
        UUID animalId = UUID.randomUUID();
        Pesagem pesagemInicial = new Pesagem(UUID.randomUUID(), animalId, LocalDate.now().minusDays(10), 180.0, false, OrigemPesagem.CADASTRO_INICIAL);
        Despesa despesaCompra = new Despesa(
                UUID.randomUUID(),
                "Compra do animal INICIAL-01",
                new BigDecimal("2500.00"),
                LocalDate.now().minusDays(10),
                CategoriaDespesa.COMPRA_ANIMAL,
                TipoDeCusto.ANIMAL,
                animalId
        );
        Animal animal = new Animal(animalId, "INICIAL-01", LocalDate.now().minusYears(1), Sexo.MACHO, Categoria.BEZERRO, Status.ATIVO, null, null);

        when(animalRepository.buscarPorId(animalId)).thenReturn(Optional.of(animal));
        when(pesagemRepository.buscarHistoricoPorAnimal(animalId)).thenReturn(List.of(pesagemInicial));
        when(despesaRepository.buscarPorAnimal(animalId)).thenReturn(List.of(despesaCompra));

        useCase.executar(animalId);

        verify(pesagemRepository).excluirPorId(pesagemInicial.getId());
        verify(despesaRepository).excluirPorId(despesaCompra.getId());
        verify(animalRepository).excluirPorId(animalId);
    }

    @Test
    @DisplayName("Despesa operacional bloqueia exclusao sem ser removida")
    void despesaOperacionalBloqueiaExclusaoSemSerRemovida() {
        UUID animalId = UUID.randomUUID();
        Animal animal = new Animal(animalId, "COM-DESPESA", LocalDate.now().minusYears(1), Sexo.MACHO, Categoria.BEZERRO, Status.ATIVO, null, null);
        Despesa despesaOperacional = new Despesa(
                UUID.randomUUID(),
                "Medicamento",
                new BigDecimal("120.00"),
                LocalDate.now().minusDays(5),
                CategoriaDespesa.SANIDADE,
                TipoDeCusto.ANIMAL,
                animalId
        );

        when(animalRepository.buscarPorId(animalId)).thenReturn(Optional.of(animal));
        when(pesagemRepository.buscarHistoricoPorAnimal(animalId)).thenReturn(List.of());
        when(despesaRepository.buscarPorAnimal(animalId)).thenReturn(List.of(despesaOperacional));

        assertThatThrownBy(() -> useCase.executar(animalId))
                .isInstanceOf(IllegalStateException.class);

        verify(animalRepository, never()).excluirPorId(animalId);
        verify(despesaRepository, never()).excluirPorId(despesaOperacional.getId());
    }

    @Test
    @DisplayName("Bloqueia exclusao de Matriz com Filhos")
    void deveBloquearExclusaoDeMatrizComFilhos() {
        UUID animalId = UUID.randomUUID();

        Animal animalAtivo = new Animal(animalId,
                "01", LocalDate.of(2024, 1, 1),
                Sexo.FEMEA, Categoria.VACA, Status.ATIVO, null, null);

        when(animalRepository.buscarPorId(animalId))
                .thenReturn(Optional.of(animalAtivo));

        when(pesagemRepository.buscarHistoricoPorAnimal(animalId))
                .thenReturn(List.of());

        when(despesaRepository.buscarPorAnimal(animalId))
                .thenReturn(List.of());

        when(animalRepository.existeFilhoComMaeId(animalId))
                .thenReturn(true);

        assertThatThrownBy(() -> useCase.executar(animalId))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("matriz");

        verify(animalRepository, never()).excluirPorId(animalId);
    }

}
