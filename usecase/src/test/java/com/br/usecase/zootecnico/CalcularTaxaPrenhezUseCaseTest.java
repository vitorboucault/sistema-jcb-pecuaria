package com.br.usecase.zootecnico;

import com.br.core.domain.enums.ResultadoDiagnostico;
import com.br.core.domain.model.DiagnosticoGestacao;
import com.br.core.domain.repository.DiagnosticoGestacaoRepository;
import com.br.core.domain.repository.EventoReprodutivoRepository;
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
class CalcularTaxaPrenhezUseCaseTest {

    @Mock
    private EventoReprodutivoRepository eventoRepository;
    @Mock
    private DiagnosticoGestacaoRepository diagnosticoRepository;

    @InjectMocks
    private CalcularTaxaPrenhezUseCase useCase;

    @Test
    @DisplayName("Deve calcular a taxa de prenhez da estação de monta")
    void deveCalcularTaxaPrenhez() {
        UUID estacaoId = UUID.randomUUID();

        // 100 fêmeas entraram em reprodução
        when(eventoRepository.contarFemeasUnicasNaEstacao(estacaoId)).thenReturn(100L);

        // Retorna uma lista com 85 diagnósticos positivos
        DiagnosticoGestacao diagnosticoPrenhe = new DiagnosticoGestacao(
                UUID.randomUUID(), UUID.randomUUID(), estacaoId, LocalDate.now(), ResultadoDiagnostico.PRENHE
        );
        List<DiagnosticoGestacao> lista = java.util.Collections.nCopies(85, diagnosticoPrenhe);

        when(diagnosticoRepository.buscarPorEstacaoMonta(estacaoId)).thenReturn(lista);

        BigDecimal taxa = useCase.executar(estacaoId);

        // Taxa = 85.00%
        assertThat(taxa).isEqualByComparingTo("85.00");
    }
}
