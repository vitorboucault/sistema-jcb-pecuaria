package com.br.usecase.nutricao;

import com.br.core.domain.enums.Categoria;
import com.br.core.domain.enums.Sexo;
import com.br.core.domain.enums.Status;
import com.br.core.domain.model.Animal;
import com.br.core.domain.model.Pesagem;
import com.br.core.domain.repository.AnimalRepository;
import com.br.core.domain.repository.PesagemRepository;
import com.br.usecase.port.FornecimentoRacaoRepositoryPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CalcularConversaoAlimentarUseCaseTest {

    @Mock
    private FornecimentoRacaoRepositoryPort fornecimentoRepository;

    @Mock
    private AnimalRepository animalRepository;

    @Mock
    private PesagemRepository pesagemRepository;

    @InjectMocks
    private CalcularConversaoAlimentarUseCase useCase;

    @Test
    @DisplayName("Deve calcular a conversão alimentar com precisão para um lote em confinamento")
    void deveCalcularConversaoAlimentarCorretamente() {
        UUID loteId = UUID.randomUUID();
        LocalDate inicio = LocalDate.now().minusDays(90);
        LocalDate fim = LocalDate.now();

        UUID animal1 = UUID.randomUUID();
        UUID animal2 = UUID.randomUUID();
        Animal boi1 = new Animal(animal1, "BOI-01", inicio.minusMonths(24), Sexo.MACHO, Categoria.BOI, Status.ATIVO, null, loteId);
        Animal boi2 = new Animal(animal2, "BOI-02", inicio.minusMonths(24), Sexo.MACHO, Categoria.BOI, Status.ATIVO, null, loteId);

        // Consumo total do lote no período: 1800 kg de Matéria Seca
        when(fornecimentoRepository.somarConsumoMateriaSecaPorLoteNoPeriodo(loteId, inicio, fim))
                .thenReturn(1800.0);
        when(animalRepository.buscarPorLote(loteId))
                .thenReturn(List.of(boi1, boi2));

        // Boi 1: ganhou 150 kg (350 -> 500)
        Pesagem p1Ini = new Pesagem(UUID.randomUUID(), animal1, inicio, 350.0, false);
        Pesagem p1Fim = new Pesagem(UUID.randomUUID(), animal1, fim, 500.0, false);
        when(pesagemRepository.buscarHistoricoPorAnimal(animal1)).thenReturn(List.of(p1Ini, p1Fim));

        // Boi 2: ganhou 150 kg (360 -> 510)
        Pesagem p2Ini = new Pesagem(UUID.randomUUID(), animal2, inicio, 360.0, false);
        Pesagem p2Fim = new Pesagem(UUID.randomUUID(), animal2, fim, 510.0, false);
        when(pesagemRepository.buscarHistoricoPorAnimal(animal2)).thenReturn(List.of(p2Ini, p2Fim));

        // Ganho total = 300 kg | Consumo MS = 1800 kg => CA = 1800 / 300 = 6.00
        BigDecimal ca = useCase.executar(loteId, inicio, fim);

        assertThat(ca).isEqualByComparingTo("6.00");
    }

    @Test
    @DisplayName("Deve retornar zero caso o lote apresente perda de peso ou ganho nulo")
    void deveRetornarZeroSePerdaDePeso() {
        UUID loteId = UUID.randomUUID();
        LocalDate inicio = LocalDate.now().minusDays(30);
        LocalDate fim = LocalDate.now();

        UUID animalId = UUID.randomUUID();
        Animal boi = new Animal(animalId, "BOI-03", inicio.minusMonths(20), Sexo.MACHO, Categoria.BOI, Status.ATIVO, null, loteId);

        when(fornecimentoRepository.somarConsumoMateriaSecaPorLoteNoPeriodo(loteId, inicio, fim))
                .thenReturn(500.0);
        when(animalRepository.buscarPorLote(loteId))
                .thenReturn(List.of(boi));

        // Animal perdeu peso (adoeceu/refechamento): 400 -> 380 kg
        Pesagem pIni = new Pesagem(UUID.randomUUID(), animalId, inicio, 400.0, false);
        Pesagem pFim = new Pesagem(UUID.randomUUID(), animalId, fim, 380.0, false);
        when(pesagemRepository.buscarHistoricoPorAnimal(animalId)).thenReturn(List.of(pIni, pFim));

        BigDecimal ca = useCase.executar(loteId, inicio, fim);

        assertThat(ca).isEqualByComparingTo(BigDecimal.ZERO);
    }
}