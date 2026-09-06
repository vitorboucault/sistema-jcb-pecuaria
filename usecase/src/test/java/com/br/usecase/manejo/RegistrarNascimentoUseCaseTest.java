package com.br.usecase.manejo;

import com.br.core.domain.enums.Categoria;
import com.br.core.domain.enums.Sexo;
import com.br.core.domain.enums.Status;
import com.br.core.domain.model.Animal;
import com.br.core.domain.repository.AnimalRepository;
import com.br.usecase.dto.RegistrarNascimentoCommand;
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
class RegistrarNascimentoUseCaseTest {

    @Mock
    private AnimalRepository animalRepository;

    @InjectMocks
    private RegistrarNascimentoUseCase useCase;

    @Test
    @DisplayName("Deve registrar nascimento de bezerro macho e alocar no lote atual da mãe")
    void deveRegistrarNascimentoComSucesso() {
        UUID maeId = UUID.randomUUID();
        UUID loteMae = UUID.randomUUID();
        Animal vaca = new Animal(maeId, "MATRIZ-01", LocalDate.now().minusYears(4), Sexo.FEMEA, Categoria.VACA, Status.ATIVO, null, loteMae);

        when(animalRepository.buscarPorBrinco("BEZ-001")).thenReturn(Optional.empty());
        when(animalRepository.buscarPorId(maeId)).thenReturn(Optional.of(vaca));

        RegistrarNascimentoCommand command = new RegistrarNascimentoCommand(
                "BEZ-001", LocalDate.now(), Sexo.MACHO, maeId
        );

        UUID bezerroId = useCase.executar(command);

        assertThat(bezerroId).isNotNull();

        ArgumentCaptor<Animal> captor = ArgumentCaptor.forClass(Animal.class);
        verify(animalRepository, times(1)).salvar(captor.capture());

        Animal bezerroSalvo = captor.getValue();
        assertThat(bezerroSalvo.getBrincoRgd()).isEqualTo("BEZ-001");
        assertThat(bezerroSalvo.getCategoriaAtual()).isEqualTo(Categoria.BEZERRO);
        assertThat(bezerroSalvo.getLoteId()).isEqualTo(loteMae);
        assertThat(bezerroSalvo.getMaeId()).isEqualTo(maeId);
    }

    @Test
    @DisplayName("Deve lançar exceção ao cadastrar bezerro com brinco já existente no rebanho")
    void deveBarrarBrincoDuplicado() {
        Animal existente = new Animal("BEZ-REPETIDO", LocalDate.now().minusMonths(2), Sexo.MACHO, null, null);
        when(animalRepository.buscarPorBrinco("BEZ-REPETIDO")).thenReturn(Optional.of(existente));

        RegistrarNascimentoCommand command = new RegistrarNascimentoCommand(
                "BEZ-REPETIDO", LocalDate.now(), Sexo.MACHO, UUID.randomUUID()
        );

        assertThatThrownBy(() -> useCase.executar(command))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Erro: Já existe um animal com o brinco");

        verify(animalRepository, never()).salvar(any());
    }

    @Test
    @DisplayName("Deve barrar registro se a mãe informada for do sexo macho")
    void deveBarrarMaeMacho() {
        UUID touroId = UUID.randomUUID();
        Animal touro = new Animal(touroId, "TOURO-01", LocalDate.now().minusYears(5), Sexo.MACHO, Categoria.TOURO, Status.ATIVO, null, null);

        when(animalRepository.buscarPorBrinco("BEZ-002")).thenReturn(Optional.empty());
        when(animalRepository.buscarPorId(touroId)).thenReturn(Optional.of(touro));

        RegistrarNascimentoCommand command = new RegistrarNascimentoCommand(
                "BEZ-002", LocalDate.now(), Sexo.FEMEA, touroId
        );

        assertThatThrownBy(() -> useCase.executar(command))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("O animal vinculado como mae nao é uma fêmea.");
    }
}