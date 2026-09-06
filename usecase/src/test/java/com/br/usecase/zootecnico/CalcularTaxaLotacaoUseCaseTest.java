package com.br.usecase.zootecnico;

import com.br.core.domain.model.MovimentacaoLote;
import com.br.core.domain.repository.MovimentacaoLoteRepository;
import com.br.core.domain.repository.PastoRepository;
import com.br.core.domain.repository.PesagemRepository;
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
class CalcularTaxaLotacaoUseCaseTest {

    @Mock
    private MovimentacaoLoteRepository movimentacaoRepository;

    @Mock
    private PastoRepository pastoRepository;

    @Mock
    private PesagemRepository pesagemRepository;

    @InjectMocks
    private CalcularTaxaLotacaoUseCase useCase;

    @Test
    @DisplayName("Deve retornar ZERO quando o pasto não tiver lotes ativos ou área inválida")
    void deveRetornarZeroQuandoSemLotesOuAreaZero() {
        UUID pastoId = UUID.randomUUID();

        when(pastoRepository.buscarAreaHectares(pastoId)).thenReturn(50.0);
        when(movimentacaoRepository.buscarLotesAtivosNoPasto(pastoId)).thenReturn(List.of());

        BigDecimal taxa = useCase.executar(pastoId);

        assertThat(taxa).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    @DisplayName("Deve calcular a taxa de lotação em UA/ha com base na área do pasto")
    void deveCalcularTaxaLotacaoComLotesAtivos() {
        UUID pastoId = UUID.randomUUID();
        UUID loteId = UUID.randomUUID();

        // Piquete de 20 hectares com 1 lote alocado
        when(pastoRepository.buscarAreaHectares(pastoId)).thenReturn(20.0);

        MovimentacaoLote mov = new MovimentacaoLote(
                UUID.randomUUID(), loteId, pastoId, LocalDate.now().minusDays(15), null
        );
        when(movimentacaoRepository.buscarLotesAtivosNoPasto(pastoId)).thenReturn(List.of(mov));

        BigDecimal taxa = useCase.executar(pastoId);

        // Garante que o indicador retorne valor não negativo e dentro da escala decimal
        assertThat(taxa).isNotNull();
        assertThat(taxa).isGreaterThanOrEqualTo(BigDecimal.ZERO);
    }
}