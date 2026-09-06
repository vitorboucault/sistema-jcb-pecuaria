package com.br.usecase.financeiro;

import com.br.core.domain.repository.DespesaRepository;
import com.br.core.domain.repository.PastoRepository;
import com.br.core.domain.repository.VendaAnimalRepository;
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
class CalcularMargemBrutaHectareUseCaseTest {

    @Mock
    private VendaAnimalRepository vendaRepository;
    @Mock
    private DespesaRepository despesaRepository;
    @Mock
    private PastoRepository pastoRepository;

    @InjectMocks
    private CalcularMargemBrutaHectareUseCase useCase;

    @Test
    @DisplayName("Deve calcular a Margem Bruta por Hectare da fazenda")
    void deveCalcularMargemBrutaPorHectare() {
        LocalDate inicio = LocalDate.of(2026, 1, 1);
        LocalDate fim = LocalDate.of(2026, 12, 31);

        // Faturamento total de vendas: R$ 300.000,00
        when(vendaRepository.somarReceitasNoPeriodo(inicio, fim)).thenReturn(new BigDecimal("300000.00"));
        // Custo operacional total: R$ 100.000,00
        when(despesaRepository.somarDespesasNoPeriodo(inicio, fim)).thenReturn(new BigDecimal("200000.00"));
        // Área total pastável: 500 hectares
        when(pastoRepository.somarAreaTotal()).thenReturn(500.0);

        BigDecimal margemBruta = useCase.executar(inicio, fim);

        // (300.000 - 100.000) / 500 ha = 200.000 / 500 = R$ 400,00 por hectare
        assertThat(margemBruta).isEqualByComparingTo("200.00");
    }
}
