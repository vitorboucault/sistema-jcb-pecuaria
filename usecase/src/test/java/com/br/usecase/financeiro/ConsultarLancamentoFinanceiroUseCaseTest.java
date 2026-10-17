package com.br.usecase.financeiro;

import com.br.core.domain.enums.CategoriaDespesa;
import com.br.core.domain.enums.SituacaoFinanceira;
import com.br.core.domain.enums.TipoDeCusto;
import com.br.core.domain.enums.TipoLancamentoFinanceiro;
import com.br.core.domain.model.LancamentoFinanceiro;
import com.br.core.domain.repository.LancamentoFinanceiroRepository;
import com.br.usecase.exception.LancamentoFinanceiroNaoEncontradoException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class ConsultarLancamentoFinanceiroUseCaseTest {
    @Test
    @DisplayName("@spec:AC-700 Consulta por UUID retorna o lançamento da porta sem salvar")
    void consultaExistente() {
        var repository = mock(LancamentoFinanceiroRepository.class);
        var lancamento = LancamentoFinanceiro.builder().tipo(TipoLancamentoFinanceiro.DESPESA)
                .descricao("Energia").valor(BigDecimal.TEN).categoria(CategoriaDespesa.ENERGIA)
                .dataReferencia(LocalDate.of(2025, 1, 10)).situacao(SituacaoFinanceira.PENDENTE)
                .tipoDeCusto(TipoDeCusto.GERAL).build();
        when(repository.buscarPorId(lancamento.getId())).thenReturn(Optional.of(lancamento));
        assertThat(new ConsultarLancamentoFinanceiroUseCase(repository).executar(lancamento.getId())).isSameAs(lancamento);
        verify(repository).buscarPorId(lancamento.getId());
        verifyNoMoreInteractions(repository);
    }

    @Test
    @DisplayName("@spec:AC-700 Consulta de UUID inexistente sinaliza recurso não encontrado")
    void consultaInexistente() {
        var repository = mock(LancamentoFinanceiroRepository.class);
        UUID id = UUID.randomUUID();
        assertThatThrownBy(() -> new ConsultarLancamentoFinanceiroUseCase(repository).executar(id))
                .isInstanceOf(LancamentoFinanceiroNaoEncontradoException.class);
        verify(repository).buscarPorId(id);
        verifyNoMoreInteractions(repository);
    }
}
