package com.br.usecase.manejo;

import com.br.core.domain.enums.Categoria;
import com.br.core.domain.enums.ModalidadeVenda;
import com.br.core.domain.enums.Sexo;
import com.br.core.domain.enums.Status;
import com.br.core.domain.model.Animal;
import com.br.core.domain.model.VendaAnimal;
import com.br.core.domain.repository.AnimalRepository;
import com.br.core.domain.repository.VendaAnimalRepository;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReverterVendaAnimalUseCaseTest {

    @Mock
    private AnimalRepository animalRepository;

    @Mock
    private VendaAnimalRepository vendaAnimalRepository;

    @InjectMocks
    private ReverterVendaAnimalUseCase useCase;

    @Test
    void deveReverterVendaEExcluirVenda() {
        UUID animalId = UUID.randomUUID();
        UUID vendaId = UUID.randomUUID();
        Animal animal = animal(animalId, Status.VENDIDO);
        VendaAnimal venda = venda(vendaId, animalId);
        when(animalRepository.buscarPorId(animalId)).thenReturn(Optional.of(animal));
        when(vendaAnimalRepository.buscarPorAnimalId(animalId)).thenReturn(List.of(venda));

        useCase.executar(animalId);

        assertThat(animal.getStatus()).isEqualTo(Status.ATIVO);
        assertThat(animal.getLoteId()).isNull();
        verify(vendaAnimalRepository).excluirPorId(vendaId);
        verify(animalRepository).salvar(animal);
    }

    @Test
    void animalInexistenteEhRejeitado() {
        UUID animalId = UUID.randomUUID();
        when(animalRepository.buscarPorId(animalId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.executar(animalId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Animal nao encontrado.");

        verify(vendaAnimalRepository, never()).buscarPorAnimalId(any());
        naoAlteraAnimalNemVenda();
    }

    @Test
    void animalAtivoEhRejeitado() {
        rejeitarStatus(Status.ATIVO);
    }

    @Test
    void animalMortoEhRejeitado() {
        rejeitarStatus(Status.MORTO);
    }

    @Test
    void vendidoSemVendaEhInconsistente() {
        rejeitarQuantidadeDeVendas(List.of());
    }

    @Test
    void vendidoComMaisDeUmaVendaEhInconsistente() {
        UUID animalId = UUID.randomUUID();
        Animal animal = animal(animalId, Status.VENDIDO);
        when(animalRepository.buscarPorId(animalId)).thenReturn(Optional.of(animal));
        when(vendaAnimalRepository.buscarPorAnimalId(animalId)).thenReturn(List.of(
                venda(UUID.randomUUID(), animalId), venda(UUID.randomUUID(), animalId)
        ));

        assertThatThrownBy(() -> useCase.executar(animalId))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Venda do animal inconsistente.");

        naoAlteraAnimalNemVenda();
    }

    private void rejeitarStatus(Status status) {
        UUID animalId = UUID.randomUUID();
        Animal animal = animal(animalId, status);
        when(animalRepository.buscarPorId(animalId)).thenReturn(Optional.of(animal));

        assertThatThrownBy(() -> useCase.executar(animalId))
                .isInstanceOf(IllegalStateException.class);

        verify(vendaAnimalRepository, never()).buscarPorAnimalId(any());
        naoAlteraAnimalNemVenda();
    }

    private void rejeitarQuantidadeDeVendas(List<VendaAnimal> vendas) {
        UUID animalId = UUID.randomUUID();
        Animal animal = animal(animalId, Status.VENDIDO);
        when(animalRepository.buscarPorId(animalId)).thenReturn(Optional.of(animal));
        when(vendaAnimalRepository.buscarPorAnimalId(animalId)).thenReturn(vendas);

        assertThatThrownBy(() -> useCase.executar(animalId))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Venda do animal inconsistente.");

        naoAlteraAnimalNemVenda();
    }

    private void naoAlteraAnimalNemVenda() {
        verify(vendaAnimalRepository, never()).excluirPorId(any());
        verify(animalRepository, never()).salvar(any());
    }

    private Animal animal(UUID id, Status status) {
        return new Animal(id, "BRINCO-" + id, LocalDate.now().minusYears(2), Sexo.MACHO,
                Categoria.BOI, status, null, null);
    }

    private VendaAnimal venda(UUID id, UUID animalId) {
        return new VendaAnimal(id, animalId, LocalDate.now(), ModalidadeVenda.POR_CABECA,
                500.0, null, new BigDecimal("1000.00"));
    }
}
