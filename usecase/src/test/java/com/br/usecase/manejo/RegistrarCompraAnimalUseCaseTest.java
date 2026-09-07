package com.br.usecase.manejo;

import com.br.core.domain.enums.Categoria;
import com.br.core.domain.enums.CategoriaDespesa;
import com.br.core.domain.enums.Sexo;
import com.br.core.domain.enums.Status;
import com.br.core.domain.enums.TipoDeCusto;
import com.br.core.domain.model.Animal;
import com.br.core.domain.model.Despesa;
import com.br.core.domain.repository.AnimalRepository;
import com.br.core.domain.repository.DespesaRepository;
import com.br.usecase.dto.RegistrarCompraAnimalCommand;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
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
class RegistrarCompraAnimalUseCaseTest {

    @Mock
    private AnimalRepository animalRepository;

    @Mock
    private DespesaRepository despesaRepository;

    @InjectMocks
    private RegistrarCompraAnimalUseCase useCase;

    @Test
    @DisplayName("Deve registrar animal comprado como ativo com categoria e lote informados")
    void deveRegistrarAnimalCompradoComSucesso() {
        UUID loteId = UUID.randomUUID();
        RegistrarCompraAnimalCommand command = new RegistrarCompraAnimalCommand(
                "COMPRA-001",
                LocalDate.now().minusYears(2),
                Sexo.MACHO,
                Categoria.BOI,
                loteId,
                null,
                null
        );

        when(animalRepository.buscarPorBrinco("COMPRA-001")).thenReturn(Optional.empty());

        UUID animalId = useCase.executar(command);

        assertThat(animalId).isNotNull();

        ArgumentCaptor<Animal> captor = ArgumentCaptor.forClass(Animal.class);
        verify(animalRepository).salvar(captor.capture());

        Animal animalSalvo = captor.getValue();
        assertThat(animalSalvo.getId()).isEqualTo(animalId);
        assertThat(animalSalvo.getBrincoRgd()).isEqualTo("COMPRA-001");
        assertThat(animalSalvo.getDataNascimento()).isEqualTo(command.dataNascimento());
        assertThat(animalSalvo.getSexo()).isEqualTo(Sexo.MACHO);
        assertThat(animalSalvo.getCategoriaAtual()).isEqualTo(Categoria.BOI);
        assertThat(animalSalvo.getStatus()).isEqualTo(Status.ATIVO);
        assertThat(animalSalvo.getMaeId()).isNull();
        assertThat(animalSalvo.getLoteId()).isEqualTo(loteId);
    }

    @Test
    @DisplayName("Deve barrar compra de animal sem brinco")
    void deveBarrarCompraSemBrinco() {
        RegistrarCompraAnimalCommand command = new RegistrarCompraAnimalCommand(
                " ",
                LocalDate.now().minusYears(1),
                Sexo.FEMEA,
                Categoria.NOVILHA,
                null,
                null,
                null
        );

        assertThatThrownBy(() -> useCase.executar(command))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("O brinco/RGD é obrigatório.");

        verify(animalRepository, never()).salvar(any());
    }

    @Test
    @DisplayName("Deve barrar compra de animal com brinco duplicado")
    void deveBarrarCompraComBrincoDuplicado() {
        Animal existente = new Animal(
                UUID.randomUUID(),
                "COMPRA-REPETIDA",
                LocalDate.now().minusYears(3),
                Sexo.FEMEA,
                Categoria.VACA,
                Status.ATIVO,
                null,
                null
        );
        RegistrarCompraAnimalCommand command = new RegistrarCompraAnimalCommand(
                "COMPRA-REPETIDA",
                LocalDate.now().minusYears(1),
                Sexo.FEMEA,
                Categoria.NOVILHA,
                null,
                null,
                null
        );

        when(animalRepository.buscarPorBrinco("COMPRA-REPETIDA")).thenReturn(Optional.of(existente));

        assertThatThrownBy(() -> useCase.executar(command))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Erro: Já existe um animal com o brinco");

        verify(animalRepository, never()).salvar(any());
    }

    @Test
    @DisplayName("Compra com valor deve gerar despesa vinculada ao animal")
    void deveGerarDespesaAoRegistrarCompraComValor() {
        LocalDate dataCompra = LocalDate.now().minusDays(3);
        RegistrarCompraAnimalCommand command = new RegistrarCompraAnimalCommand(
                "COMPRA-COM-VALOR",
                LocalDate.now().minusYears(2),
                Sexo.MACHO,
                Categoria.BOI,
                UUID.randomUUID(),
                dataCompra,
                new BigDecimal("3500.00")
        );

        when(animalRepository.buscarPorBrinco("COMPRA-COM-VALOR")).thenReturn(Optional.empty());

        UUID animalId = useCase.executar(command);

        ArgumentCaptor<Despesa> captor = ArgumentCaptor.forClass(Despesa.class);
        verify(despesaRepository).salvar(captor.capture());

        Despesa despesa = captor.getValue();
        assertThat(despesa.getValor()).isEqualByComparingTo("3500.00");
        assertThat(despesa.getDataOcorrencia()).isEqualTo(dataCompra);
        assertThat(despesa.getCategoria()).isEqualTo(CategoriaDespesa.COMPRA_ANIMAL);
        assertThat(despesa.getTipoCentroCusto()).isEqualTo(TipoDeCusto.ANIMAL);
        assertThat(despesa.getReferenciaId()).isEqualTo(animalId);
    }

    @Test
    @DisplayName("Compra sem valor nao deve gerar despesa")
    void naoDeveGerarDespesaAoRegistrarCompraSemValor() {
        RegistrarCompraAnimalCommand command = new RegistrarCompraAnimalCommand(
                "COMPRA-SEM-VALOR",
                LocalDate.now().minusYears(2),
                Sexo.FEMEA,
                Categoria.NOVILHA,
                UUID.randomUUID(),
                LocalDate.now(),
                null
        );

        when(animalRepository.buscarPorBrinco("COMPRA-SEM-VALOR")).thenReturn(Optional.empty());

        useCase.executar(command);

        verify(despesaRepository, never()).salvar(any());
    }

    @Test
    @DisplayName("Compra com valor menor ou igual a zero deve ser rejeitada")
    void deveRejeitarCompraComValorMenorOuIgualAZero() {
        RegistrarCompraAnimalCommand command = new RegistrarCompraAnimalCommand(
                "COMPRA-VALOR-ZERO",
                LocalDate.now().minusYears(2),
                Sexo.MACHO,
                Categoria.BOI,
                UUID.randomUUID(),
                LocalDate.now(),
                BigDecimal.ZERO
        );

        assertThatThrownBy(() -> useCase.executar(command))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("O valor da compra deve ser maior que zero.");

        verify(animalRepository, never()).salvar(any());
        verify(despesaRepository, never()).salvar(any());
    }
}
