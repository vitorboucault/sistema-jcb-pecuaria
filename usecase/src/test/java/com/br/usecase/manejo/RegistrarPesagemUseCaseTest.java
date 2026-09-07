package com.br.usecase.manejo;

import com.br.core.domain.enums.Categoria;
import com.br.core.domain.enums.OrigemPesagem;
import com.br.core.domain.enums.Sexo;
import com.br.core.domain.enums.Status;
import com.br.core.domain.model.Animal;
import com.br.core.domain.model.Pesagem;
import com.br.core.domain.repository.AnimalRepository;
import com.br.core.domain.repository.PesagemRepository;
import com.br.core.domain.service.CalculadoraGmdService;
import com.br.usecase.dto.RegistrarPesagemCommand;
import com.br.usecase.dto.RegistrarPesagemResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegistrarPesagemUseCaseTest {

    @Mock
    private AnimalRepository animalRepository;

    @Mock
    private PesagemRepository pesagemRepository;

    @Spy
    private CalculadoraGmdService calculadoraGmd = new CalculadoraGmdService();

    @InjectMocks
    private RegistrarPesagemUseCase useCase;

    @Test
    @DisplayName("Deve registrar pesagem de animal calculando o GMD exato frente à última pesagem")
    void deveRegistrarPesagemECalcularGmd() {
        UUID animalId = UUID.randomUUID();
        LocalDate dataAnterior = LocalDate.now().minusDays(60);
        LocalDate dataAtual = LocalDate.now();

        Animal boi = new Animal(animalId, "BOI-GMD", dataAnterior.minusMonths(18), Sexo.MACHO, Categoria.BOI, Status.ATIVO, null, null);
        Pesagem pesagemAnterior = new Pesagem(UUID.randomUUID(), animalId, dataAnterior, 400.0, false);

        when(animalRepository.buscarPorId(animalId)).thenReturn(Optional.of(boi));
        when(pesagemRepository.buscarUltimaPesagemDoAnimal(animalId)).thenReturn(Optional.of(pesagemAnterior));

        // Ganhou 60 kg em 60 dias (400 kg -> 460 kg) = GMD de 1.000 kg/dia
        RegistrarPesagemCommand command = new RegistrarPesagemCommand(animalId, dataAtual, 460.0, false);

        RegistrarPesagemResult result = useCase.executar(command);

        assertThat(result.pesoKg()).isEqualTo(460.0);
        assertThat(result.gmd()).isEqualTo(1.0);
        verify(pesagemRepository, times(1)).salvar(any(Pesagem.class));
        verify(pesagemRepository).salvar(argThat(pesagem -> pesagem.getOrigem() == OrigemPesagem.OPERACIONAL));
    }

    @Test
    @DisplayName("Deve registrar pesagem do cadastro com origem inicial")
    void deveRegistrarPesagemInicialComOrigemDeCadastro() {
        UUID animalId = UUID.randomUUID();
        Animal animal = new Animal(animalId, "CADASTRO-PESO", LocalDate.now().minusYears(1), Sexo.MACHO,
                Categoria.BEZERRO, Status.ATIVO, null, null);

        when(animalRepository.buscarPorId(animalId)).thenReturn(Optional.of(animal));

        useCase.executar(new RegistrarPesagemCommand(
                animalId, LocalDate.now(), 180.0, true, OrigemPesagem.CADASTRO_INICIAL));

        verify(pesagemRepository).salvar(argThat(pesagem -> pesagem.getOrigem() == OrigemPesagem.CADASTRO_INICIAL));
    }

    @Test
    @DisplayName("Deve lançar erro ao tentar registrar pesagem para animal inexistente")
    void deveBarrarAnimalInexistente() {
        UUID animalId = UUID.randomUUID();
        when(animalRepository.buscarPorId(animalId)).thenReturn(Optional.empty());

        RegistrarPesagemCommand command = new RegistrarPesagemCommand(animalId, LocalDate.now(), 300.0, false);

        assertThatThrownBy(() -> useCase.executar(command))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Erro: Animal nao encontrado no sistema.");

        verify(pesagemRepository, never()).salvar(any());
    }

    @Test
    @DisplayName("Não deve registrar pesagem para animal morto")
    void deveBarrarPesagemDeAnimalMorto() {
        UUID animalId = UUID.randomUUID();
        Animal animal = new Animal(animalId, "MORTO-PESO", LocalDate.now().minusYears(2), Sexo.MACHO,
                Categoria.BOI, Status.MORTO, null, null, LocalDate.now().minusDays(1));
        when(animalRepository.buscarPorId(animalId)).thenReturn(Optional.of(animal));

        assertThatThrownBy(() -> useCase.executar(new RegistrarPesagemCommand(
                animalId, LocalDate.now(), 400.0, false)))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Somente animais ativos podem receber pesagem.");

        verify(pesagemRepository, never()).buscarUltimaPesagemDoAnimal(animalId);
        verify(pesagemRepository, never()).salvar(any());
    }

    @Test
    @DisplayName("Não deve registrar pesagem para animal vendido")
    void deveBarrarPesagemDeAnimalVendido() {
        UUID animalId = UUID.randomUUID();
        Animal animal = new Animal(animalId, "VENDIDO-PESO", LocalDate.now().minusYears(2), Sexo.MACHO,
                Categoria.BOI, Status.VENDIDO, null, null);
        when(animalRepository.buscarPorId(animalId)).thenReturn(Optional.of(animal));

        assertThatThrownBy(() -> useCase.executar(new RegistrarPesagemCommand(
                animalId, LocalDate.now(), 400.0, false)))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Somente animais ativos podem receber pesagem.");

        verify(pesagemRepository, never()).buscarUltimaPesagemDoAnimal(animalId);
        verify(pesagemRepository, never()).salvar(any());
    }
}
