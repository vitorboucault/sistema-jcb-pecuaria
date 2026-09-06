package com.br.usecase.financeiro;

import com.br.core.domain.repository.AnimalRepository;
import com.br.core.domain.repository.DespesaRepository;
import com.br.core.domain.repository.PesagemRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.time.LocalDate;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CalcularCustoArrobaUseCaseTest {

    @Mock
    private DespesaRepository despesaRepository;
    @Mock
    private PesagemRepository pesagemRepository;
    @Mock
    private AnimalRepository animalRepository;

    @InjectMocks
    private CalcularCustoArrobaUseCase useCase;

    @Test
    @DisplayName("Deve calcular o custo da arroba produzida global na safra")
    void deveCalcularCustoArrobaGlobal() {
        LocalDate inicio = LocalDate.of(2026, 1, 1);
        LocalDate fim = LocalDate.of(2026, 12, 31);

        // Custo total da fazenda no período: R$ 15.000,00
        when(despesaRepository.somarDespesasNoPeriodo(inicio, fim)).thenReturn(new BigDecimal("15000.00"));
        // Ganho de peso total do rebanho: 3.000 kg (100 arrobas)
        when(pesagemRepository.calcularGanhoPesoTotalNoPeriodo(inicio, fim)).thenReturn(3000.0);

        BigDecimal custoArroba = useCase.executar(inicio, fim);

        // 15.000 / 100 arrobas = 150,00 por arroba produzida
        assertThat(custoArroba).isEqualByComparingTo("150.00");
    }
}
