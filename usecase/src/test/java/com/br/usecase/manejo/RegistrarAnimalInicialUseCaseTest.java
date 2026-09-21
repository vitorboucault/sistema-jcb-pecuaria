package com.br.usecase.manejo;

import com.br.core.domain.enums.Categoria;
import com.br.core.domain.enums.OrigemAnimal;
import com.br.core.domain.enums.Sexo;
import com.br.core.domain.enums.Status;
import com.br.core.domain.model.Animal;
import com.br.core.domain.repository.AnimalRepository;
import com.br.usecase.dto.RegistrarAnimalInicialCommand;
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
class RegistrarAnimalInicialUseCaseTest {

    @Mock
    private AnimalRepository animalRepository;

    @InjectMocks
    private RegistrarAnimalInicialUseCase useCase;

    @Test
    @DisplayName("@spec:AC-320 Cadastro inicial preserva origem NASCIMENTO")
    void deveRegistrarAnimalInicialComOrigemNascimento() {
        UUID loteId = UUID.randomUUID();
        RegistrarAnimalInicialCommand command = commandValido(OrigemAnimal.NASCIMENTO, loteId);
        when(animalRepository.buscarPorBrinco(command.brincoRgd())).thenReturn(Optional.empty());

        UUID animalId = useCase.executar(command);

        ArgumentCaptor<Animal> captor = ArgumentCaptor.forClass(Animal.class);
        verify(animalRepository).salvar(captor.capture());

        Animal animalSalvo = captor.getValue();
        assertThat(animalSalvo.getId()).isEqualTo(animalId);
        assertThat(animalSalvo.getStatus()).isEqualTo(Status.ATIVO);
        assertThat(animalSalvo.getCategoriaAtual()).isEqualTo(command.categoria());
        assertThat(animalSalvo.getSexo()).isEqualTo(command.sexo());
        assertThat(animalSalvo.getLoteId()).isEqualTo(loteId);
        assertThat(animalSalvo.getMaeId()).isNull();
        assertThat(animalSalvo.getDataMorte()).isNull();
        assertThat(animalSalvo.getOrigem()).isEqualTo(OrigemAnimal.NASCIMENTO);
    }

    @Test
    @DisplayName("@spec:AC-321 Cadastro inicial aceita origem DESCONHECIDO")
    void deveRegistrarAnimalInicialComOrigemDesconhecida() {
        RegistrarAnimalInicialCommand command = commandValido(OrigemAnimal.DESCONHECIDO, null);
        when(animalRepository.buscarPorBrinco(command.brincoRgd())).thenReturn(Optional.empty());

        useCase.executar(command);

        ArgumentCaptor<Animal> captor = ArgumentCaptor.forClass(Animal.class);
        verify(animalRepository).salvar(captor.capture());
        assertThat(captor.getValue().getOrigem()).isEqualTo(OrigemAnimal.DESCONHECIDO);
        assertThat(captor.getValue().getLoteId()).isNull();
    }

    @Test
    @DisplayName("@spec:AC-322 Cadastro inicial com origem COMPRA não gera efeito financeiro")
    void deveRegistrarCompraHistoricaSemAcionarCompraOperacional() {
        RegistrarAnimalInicialCommand command = commandValido(OrigemAnimal.COMPRA, UUID.randomUUID());
        when(animalRepository.buscarPorBrinco(command.brincoRgd())).thenReturn(Optional.empty());

        useCase.executar(command);

        ArgumentCaptor<Animal> captor = ArgumentCaptor.forClass(Animal.class);
        verify(animalRepository).salvar(captor.capture());
        assertThat(captor.getValue().getOrigem()).isEqualTo(OrigemAnimal.COMPRA);
    }

    @Test
    @DisplayName("@spec:AC-323 Cadastro inicial rejeita brinco duplicado")
    void deveRejeitarCadastroInicialComBrincoDuplicado() {
        RegistrarAnimalInicialCommand command = commandValido(OrigemAnimal.NASCIMENTO, null);
        when(animalRepository.buscarPorBrinco(command.brincoRgd())).thenReturn(Optional.of(animalExistente(command.brincoRgd())));

        assertThatThrownBy(() -> useCase.executar(command))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Erro: Já existe um animal com o brinco");

        verify(animalRepository, never()).salvar(any());
    }

    @Test
    @DisplayName("@spec:AC-324 Cadastro inicial rejeita brinco vazio antes da persistência")
    void deveRejeitarBrincoVazio() {
        RegistrarAnimalInicialCommand command = new RegistrarAnimalInicialCommand(
                " ", LocalDate.now().minusYears(3), Sexo.FEMEA, Categoria.VACA, null, OrigemAnimal.NASCIMENTO
        );

        assertThatThrownBy(() -> useCase.executar(command))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("O brinco/RGD é obrigatório.");

        verify(animalRepository, never()).salvar(any());
    }

    @Test
    @DisplayName("@spec:AC-324 Cadastro inicial rejeita data de nascimento nula antes da persistência")
    void deveRejeitarDataNascimentoNula() {
        RegistrarAnimalInicialCommand command = new RegistrarAnimalInicialCommand(
                "INI-002", null, Sexo.FEMEA, Categoria.VACA, null, OrigemAnimal.NASCIMENTO
        );

        assertThatThrownBy(() -> useCase.executar(command))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("A data de nascimento é obrigatória.");

        verify(animalRepository, never()).salvar(any());
    }

    @Test
    @DisplayName("@spec:AC-324 Cadastro inicial rejeita data de nascimento futura antes da persistência")
    void deveRejeitarDataNascimentoFutura() {
        RegistrarAnimalInicialCommand command = new RegistrarAnimalInicialCommand(
                "INI-003", LocalDate.now().plusDays(1), Sexo.FEMEA, Categoria.VACA, null, OrigemAnimal.NASCIMENTO
        );

        assertThatThrownBy(() -> useCase.executar(command))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("O bezerro nao pode nascer no futuro.");

        verify(animalRepository, never()).salvar(any());
    }

    @Test
    @DisplayName("@spec:AC-324 Cadastro inicial rejeita sexo nulo antes da persistência")
    void deveRejeitarSexoNulo() {
        RegistrarAnimalInicialCommand command = new RegistrarAnimalInicialCommand(
                "INI-004", LocalDate.now().minusYears(3), null, Categoria.VACA, null, OrigemAnimal.NASCIMENTO
        );

        assertThatThrownBy(() -> useCase.executar(command))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("O sexo é obrigatório.");

        verify(animalRepository, never()).salvar(any());
    }

    @Test
    @DisplayName("@spec:AC-324 Cadastro inicial rejeita categoria nula antes da persistência")
    void deveRejeitarCategoriaNula() {
        RegistrarAnimalInicialCommand command = new RegistrarAnimalInicialCommand(
                "INI-005", LocalDate.now().minusYears(3), Sexo.FEMEA, null, null, OrigemAnimal.NASCIMENTO
        );

        assertThatThrownBy(() -> useCase.executar(command))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("A categoria é obrigatória.");

        verify(animalRepository, never()).salvar(any());
    }

    @Test
    @DisplayName("@spec:AC-324 Cadastro inicial rejeita origem nula antes da persistência")
    void deveRejeitarOrigemNula() {
        RegistrarAnimalInicialCommand command = new RegistrarAnimalInicialCommand(
                "INI-006", LocalDate.now().minusYears(3), Sexo.FEMEA, Categoria.VACA, null, null
        );

        assertThatThrownBy(() -> useCase.executar(command))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("A origem é obrigatória.");

        verify(animalRepository, never()).salvar(any());
    }

    private RegistrarAnimalInicialCommand commandValido(OrigemAnimal origem, UUID loteId) {
        return new RegistrarAnimalInicialCommand(
                "INI-001", LocalDate.now().minusYears(4), Sexo.FEMEA, Categoria.VACA, loteId, origem
        );
    }

    private Animal animalExistente(String brincoRgd) {
        return new Animal(
                UUID.randomUUID(), brincoRgd, LocalDate.now().minusYears(4), Sexo.FEMEA,
                Categoria.VACA, Status.ATIVO, null, null
        );
    }
}
