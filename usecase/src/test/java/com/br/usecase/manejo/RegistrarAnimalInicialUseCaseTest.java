package com.br.usecase.manejo;

import com.br.core.domain.enums.Categoria;
import com.br.core.domain.enums.OrigemAnimal;
import com.br.core.domain.enums.OrigemPesagem;
import com.br.core.domain.enums.Sexo;
import com.br.core.domain.enums.Status;
import com.br.core.domain.model.Animal;
import com.br.core.domain.model.Pesagem;
import com.br.core.domain.repository.AnimalRepository;
import com.br.core.domain.repository.PesagemRepository;
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
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegistrarAnimalInicialUseCaseTest {

    @Mock
    private AnimalRepository animalRepository;

    @Mock
    private PesagemRepository pesagemRepository;

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
                .hasMessage("A data de nascimento não pode ser futura.");

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

    @Test
    @DisplayName("@spec:AC-334 Cadastro com peso cria pesagem inicial")
    void deveCriarPesagemInicialComOrigemDeCadastro() {
        LocalDate dataPesagem = LocalDate.of(2026, 9, 20);
        RegistrarAnimalInicialCommand command = commandComPesagem(OrigemAnimal.DESCONHECIDO, 385.5, dataPesagem);
        when(animalRepository.buscarPorBrinco(command.brincoRgd())).thenReturn(Optional.empty());

        UUID animalId = useCase.executar(command);

        ArgumentCaptor<Pesagem> captor = ArgumentCaptor.forClass(Pesagem.class);
        verify(pesagemRepository).salvar(captor.capture());
        Pesagem pesagem = captor.getValue();
        assertThat(pesagem.getAnimalId()).isEqualTo(animalId);
        assertThat(pesagem.getPeso()).isEqualTo(385.5);
        assertThat(pesagem.getDataPesagem()).isEqualTo(dataPesagem);
        assertThat(pesagem.getOrigem()).isEqualTo(OrigemPesagem.CADASTRO_INICIAL);
    }

    @Test
    @DisplayName("@spec:AC-335 Peso atual é opcional e não cria pesagem")
    void deveCadastrarSemCriarPesagemQuandoPesoAusente() {
        RegistrarAnimalInicialCommand command = commandValido(OrigemAnimal.DESCONHECIDO, null);
        when(animalRepository.buscarPorBrinco(command.brincoRgd())).thenReturn(Optional.empty());

        useCase.executar(command);

        verifyNoInteractions(pesagemRepository);
    }

    @Test
    @DisplayName("@spec:AC-336 Peso atual e data da pesagem formam um par")
    void deveRejeitarPesoOuDataInformadosIsoladamente() {
        RegistrarAnimalInicialCommand somentePeso = commandComPesagem(
                OrigemAnimal.DESCONHECIDO, 385.5, null
        );
        RegistrarAnimalInicialCommand somenteData = commandComPesagem(
                OrigemAnimal.DESCONHECIDO, null, LocalDate.of(2026, 9, 20)
        );
        when(animalRepository.buscarPorBrinco(somentePeso.brincoRgd())).thenReturn(Optional.empty());
        when(animalRepository.buscarPorBrinco(somenteData.brincoRgd())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.executar(somentePeso))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("O peso atual e a data da pesagem devem ser informados juntos.");
        assertThatThrownBy(() -> useCase.executar(somenteData))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("O peso atual e a data da pesagem devem ser informados juntos.");

        verify(animalRepository, never()).salvar(any());
        verifyNoInteractions(pesagemRepository);
    }

    @Test
    @DisplayName("@spec:AC-337 Pesagem rejeita peso e datas inválidos")
    void deveRejeitarPesagemInvalidaAntesDaPersistencia() {
        LocalDate nascimento = LocalDate.now().minusYears(4);
        assertThatThrownBy(() -> executarComPesagem(0, nascimento, nascimento))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("O peso atual deve ser maior que zero.");
        assertThatThrownBy(() -> executarComPesagem(-1, nascimento, nascimento))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("O peso atual deve ser maior que zero.");
        assertThatThrownBy(() -> executarComPesagem(385.5, nascimento, LocalDate.now().plusDays(1)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("A data da pesagem não pode ser futura.");
        assertThatThrownBy(() -> executarComPesagem(385.5, nascimento, nascimento.minusDays(1)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("A data da pesagem não pode ser anterior à data de nascimento.");

        verify(animalRepository, never()).salvar(any());
        verifyNoInteractions(pesagemRepository);
    }

    @Test
    @DisplayName("@spec:AC-339 Peso atual preserva a origem do animal")
    void devePreservarOrigemDoAnimalAoCriarPesagemInicial() {
        RegistrarAnimalInicialCommand command = commandComPesagem(
                OrigemAnimal.COMPRA, 385.5, LocalDate.of(2026, 9, 20)
        );
        when(animalRepository.buscarPorBrinco(command.brincoRgd())).thenReturn(Optional.empty());

        useCase.executar(command);

        ArgumentCaptor<Animal> animalCaptor = ArgumentCaptor.forClass(Animal.class);
        ArgumentCaptor<Pesagem> pesagemCaptor = ArgumentCaptor.forClass(Pesagem.class);
        verify(animalRepository).salvar(animalCaptor.capture());
        verify(pesagemRepository).salvar(pesagemCaptor.capture());
        assertThat(animalCaptor.getValue().getOrigem()).isEqualTo(OrigemAnimal.COMPRA);
        assertThat(pesagemCaptor.getValue().getOrigem()).isEqualTo(OrigemPesagem.CADASTRO_INICIAL);
    }

    private void executarComPesagem(double peso, LocalDate nascimento, LocalDate dataPesagem) {
        RegistrarAnimalInicialCommand command = commandComPesagem(
                OrigemAnimal.DESCONHECIDO, peso, dataPesagem, nascimento
        );
        when(animalRepository.buscarPorBrinco(command.brincoRgd())).thenReturn(Optional.empty());
        useCase.executar(command);
    }

    private RegistrarAnimalInicialCommand commandComPesagem(
            OrigemAnimal origem, Double pesoAtual, LocalDate dataPesagem
    ) {
        return commandComPesagem(origem, pesoAtual, dataPesagem, LocalDate.now().minusYears(4));
    }

    private RegistrarAnimalInicialCommand commandComPesagem(
            OrigemAnimal origem, Double pesoAtual, LocalDate dataPesagem, LocalDate dataNascimento
    ) {
        return new RegistrarAnimalInicialCommand(
                "INI-PESO-" + UUID.randomUUID(), dataNascimento, Sexo.FEMEA, Categoria.VACA,
                null, origem, pesoAtual, dataPesagem
        );
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
