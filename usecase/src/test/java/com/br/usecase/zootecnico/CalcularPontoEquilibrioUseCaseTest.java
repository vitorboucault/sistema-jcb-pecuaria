package com.br.usecase.zootecnico;

import com.br.core.domain.repository.DespesaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CalcularPontoEquilibrioUseCaseTest {

    @Mock
    private DespesaRepository despesaRepository;

    @InjectMocks
    private CalcularPontoEquilibrioUseCase useCase;

    @Test
    @DisplayName("Deve calcular o Ponto de Equilíbrio em arrobas (Custo Fixo / Preço da Arroba)")
    void deveCalcularPontoDeEquilibrio() {
        LocalDate inicio = LocalDate.of(2026, 1, 1);
        LocalDate fim = LocalDate.of(2026, 12, 31);
        BigDecimal precoArroba = new BigDecimal("250.00");

        // Simula que a fazenda gastou R$ 100.000,00 no ano
        when(despesaRepository.somarDespesasNoPeriodo(inicio, fim)).thenReturn(new BigDecimal("100000.00"));

        BigDecimal pontoEquilibrio = useCase.executar(inicio, fim, precoArroba);

        // 100.000 / 250 = 400 arrobas para empatar
        assertThat(pontoEquilibrio).isEqualByComparingTo("400.00");
    }
}
