package com.br.usecase.zootecnico;

import com.br.core.domain.repository.DesmameRepository;
import com.br.core.domain.repository.EventoReprodutivoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CalcularTaxaDesmameUseCaseTest {

    @Mock
    private EventoReprodutivoRepository eventoRepository;

    @Mock
    private DesmameRepository desmameRepository;

    @InjectMocks
    private CalcularTaxaDesmameUseCase useCase;

    @Test
    @DisplayName("Deve calcular taxa de desmame correta (75 bezerros desmamados de 100 vacas expostas = 75.00%)")
    void deveCalcularTaxaDesmame() {
        UUID estacaoId = UUID.randomUUID();

        when(eventoRepository.contarFemeasUnicasNaEstacao(estacaoId)).thenReturn(100L);
        when(desmameRepository.contarDesmamesPorEstacao(estacaoId)).thenReturn(75L);

        BigDecimal taxa = useCase.executar(estacaoId);

        assertThat(taxa).isEqualByComparingTo("75.00");
    }

    @Test
    @DisplayName("Deve retornar ZERO quando não houver fêmeas expostas na estação")
    void deveRetornarZeroSemFemeas() {
        UUID estacaoId = UUID.randomUUID();

        when(eventoRepository.contarFemeasUnicasNaEstacao(estacaoId)).thenReturn(0L);

        BigDecimal taxa = useCase.executar(estacaoId);

        assertThat(taxa).isEqualByComparingTo(BigDecimal.ZERO);
    }
}
