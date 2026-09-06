package com.br.usecase.manejo;

import com.br.core.domain.enums.Categoria;
import com.br.core.domain.enums.ModalidadeVenda;
import com.br.core.domain.enums.Sexo;
import com.br.core.domain.enums.Status;
import com.br.core.domain.model.Animal;
import com.br.core.domain.repository.AnimalRepository;
import com.br.core.domain.repository.VendaAnimalRepository;
import com.br.usecase.dto.RegistrarVendaCommand;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegistrarVendaAnimalUseCaseTest {

    @Mock
    private VendaAnimalRepository vendaRepository;

    @Mock
    private AnimalRepository animalRepository;

    @InjectMocks
    private RegistrarVendaAnimalUseCase useCase;

    @Test
    @DisplayName("Deve registrar a venda para frigorífico, atualizar status para VENDIDO e desvincular do lote")
    void deveRegistrarVendaComSucesso() {
        UUID animalId = UUID.randomUUID();
        UUID loteId = UUID.randomUUID();
        Animal boi = new Animal(animalId, "BOI-ABATE-01", LocalDate.now().minusMonths(30), Sexo.MACHO, Categoria.BOI, Status.ATIVO, null, loteId);

        RegistrarVendaCommand command = new RegistrarVendaCommand(
                animalId, LocalDate.now(), ModalidadeVenda.FRIGORIFICO, 540.0, 54.0, new BigDecimal("310.00")
        );

        when(animalRepository.buscarPorId(animalId)).thenReturn(Optional.of(boi));

        UUID vendaId = useCase.executar(command);

        assertThat(vendaId).isNotNull();
        assertThat(boi.getStatus()).isEqualTo(Status.VENDIDO);
        assertThat(boi.getLoteId()).isNull();

        verify(vendaRepository, times(1)).salvar(any());
        verify(animalRepository, times(1)).salvar(boi);
    }

    @Test
    @DisplayName("Não deve permitir venda de animal que já está morto ou vendido")
    void naoDeveVenderAnimalInativo() {
        UUID animalId = UUID.randomUUID();
        Animal boiMorto = new Animal(animalId, "BOI-BAIXA", LocalDate.now().minusMonths(20), Sexo.MACHO, Categoria.BOI, Status.MORTO, null, null);

        RegistrarVendaCommand command = new RegistrarVendaCommand(
                animalId, LocalDate.now(), ModalidadeVenda.PESO, 450.0, null, new BigDecimal("12.50")
        );

        when(animalRepository.buscarPorId(animalId)).thenReturn(Optional.of(boiMorto));

        assertThatThrownBy(() -> useCase.executar(command))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Apenas animais ATIVOS podem ser vendidos.");

        verify(vendaRepository, never()).salvar(any());
    }
}
