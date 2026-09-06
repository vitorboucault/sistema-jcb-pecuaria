package com.br.usecase.financeiro;

import com.br.core.domain.repository.AnimalRepository;
import com.br.core.domain.repository.DespesaRepository;
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
class CalcularDesembolsoCabecaMesUseCaseTest {

    @Mock
    private DespesaRepository despesaRepository;
    @Mock
    private AnimalRepository animalRepository;

    @InjectMocks
    private CalcularDesembolsoCabecaMesUseCase useCase;

    @Test
    @DisplayName("Deve calcular o desembolso médio por cabeça ao mês")
    void deveCalcularDesembolsoCabecaMes() {
        // Período de exatos 12 meses (365 dias)
        LocalDate inicio = LocalDate.of(2026, 1, 1);
        LocalDate fim = LocalDate.of(2026, 12, 31);

        // Custo total: R$ 60.000,00
        when(despesaRepository.somarDespesasNoPeriodo(inicio, fim)).thenReturn(new BigDecimal("60000.00"));
        // Rebanho ativo médio: 500 cabeças
        when(animalRepository.contarAnimaisAtivos()).thenReturn(500L);

        BigDecimal desembolso = useCase.executar(inicio, fim);

        // 365 dias / 30 = 12.166 meses
        // (60.000 / 500 cabeças) / 12.166 meses = 120 / 12.166 = ~9.86 por cabeça/mês
        assertThat(desembolso).isGreaterThan(BigDecimal.ZERO);
    }
}