package com.br.usecase.financeiro;

import com.br.core.domain.enums.CategoriaDespesa;
import com.br.core.domain.enums.CategoriaReceita;
import com.br.core.domain.enums.SituacaoFinanceira;
import com.br.core.domain.enums.TipoDeCusto;
import com.br.core.domain.enums.TipoLancamentoFinanceiro;
import com.br.core.domain.model.LancamentoFinanceiro;
import com.br.core.domain.repository.LancamentoFinanceiroRepository;
import com.br.usecase.exception.LancamentoFinanceiroNaoEncontradoException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class ConfirmarLancamentoFinanceiroUseCaseTest {
    private static final LocalDate DATA = LocalDate.of(2025, 1, 10);

    @ParameterizedTest
    @EnumSource(TipoLancamentoFinanceiro.class)
    @DisplayName("@spec:AC-703 Casos de uso buscam com bloqueio antes de salvar a confirmação integral")
    void confirma(TipoLancamentoFinanceiro tipo) {
        var repository = mock(LancamentoFinanceiroRepository.class);
        var original = pendente(tipo);
        when(repository.buscarPorIdParaAtualizacao(original.getId())).thenReturn(Optional.of(original));
        var confirmado = executar(repository, tipo, original.getId(), DATA);
        var ordem = inOrder(repository);
        ordem.verify(repository).buscarPorIdParaAtualizacao(original.getId());
        ordem.verify(repository).salvar(confirmado);
        verifyNoMoreInteractions(repository);
        assertThat(confirmado).usingRecursiveComparison().ignoringFields("situacao", "dataEfetivacao").isEqualTo(original);
        assertThat(confirmado.getSituacao()).isEqualTo(SituacaoFinanceira.EFETIVADO);
        assertThat(confirmado.getDataEfetivacao()).isEqualTo(DATA);
    }

    @ParameterizedTest
    @EnumSource(TipoLancamentoFinanceiro.class)
    @DisplayName("@spec:AC-704 Repetição da mesma data retorna sem chamar salvar")
    void idempotenciaSemSalvar(TipoLancamentoFinanceiro tipo) {
        var repository = mock(LancamentoFinanceiroRepository.class);
        var original = pendente(tipo);
        var efetivado = tipo == TipoLancamentoFinanceiro.DESPESA
                ? original.confirmarPagamento(DATA) : original.confirmarRecebimento(DATA);
        when(repository.buscarPorIdParaAtualizacao(efetivado.getId())).thenReturn(Optional.of(efetivado));
        assertThat(executar(repository, tipo, efetivado.getId(), DATA)).isSameAs(efetivado);
        verify(repository).buscarPorIdParaAtualizacao(efetivado.getId());
        verifyNoMoreInteractions(repository);
    }

    @ParameterizedTest
    @EnumSource(TipoLancamentoFinanceiro.class)
    @DisplayName("@spec:AC-703 Ausência do lançamento é erro específico sem persistência")
    void inexistente(TipoLancamentoFinanceiro tipo) {
        var repository = mock(LancamentoFinanceiroRepository.class);
        UUID id = UUID.randomUUID();
        assertThatThrownBy(() -> executar(repository, tipo, id, DATA))
                .isInstanceOf(LancamentoFinanceiroNaoEncontradoException.class);
        verify(repository).buscarPorIdParaAtualizacao(id);
        verifyNoMoreInteractions(repository);
    }

    @ParameterizedTest
    @EnumSource(TipoLancamentoFinanceiro.class)
    @DisplayName("@spec:AC-703 @spec:AC-704 Falhas de data e tipo nunca chamam salvar")
    void falhasNaoSalvam(TipoLancamentoFinanceiro tipo) {
        var repository = mock(LancamentoFinanceiroRepository.class);
        var original = pendente(tipo);
        when(repository.buscarPorIdParaAtualizacao(original.getId())).thenReturn(Optional.of(original));
        assertThatThrownBy(() -> executar(repository, tipo, original.getId(), null)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> executar(repository, tipo, original.getId(),
                LocalDate.now(ZoneId.of("America/Bahia")).plusDays(2))).isInstanceOf(IllegalArgumentException.class);
        var errado = tipo == TipoLancamentoFinanceiro.DESPESA ? TipoLancamentoFinanceiro.RECEITA : TipoLancamentoFinanceiro.DESPESA;
        assertThatThrownBy(() -> executar(repository, errado, original.getId(), DATA)).isInstanceOf(IllegalStateException.class);
        var efetivado = tipo == TipoLancamentoFinanceiro.DESPESA
                ? original.confirmarPagamento(DATA) : original.confirmarRecebimento(DATA);
        when(repository.buscarPorIdParaAtualizacao(original.getId())).thenReturn(Optional.of(efetivado));
        assertThatThrownBy(() -> executar(repository, tipo, original.getId(), DATA.plusDays(1))).isInstanceOf(IllegalStateException.class);
        verify(repository, never()).salvar(any());
        verify(repository, never()).buscarPorId(any());
    }

    private LancamentoFinanceiro pendente(TipoLancamentoFinanceiro tipo) {
        return new LancamentoFinanceiro(tipo, "Integral", new BigDecimal("100.00"),
                tipo == TipoLancamentoFinanceiro.DESPESA ? CategoriaDespesa.ENERGIA : CategoriaReceita.VENDA_LEITE,
                DATA, null, SituacaoFinanceira.PENDENTE, null, TipoDeCusto.GERAL, null);
    }

    private LancamentoFinanceiro executar(LancamentoFinanceiroRepository repository, TipoLancamentoFinanceiro tipo,
                                        UUID id, LocalDate data) {
        return tipo == TipoLancamentoFinanceiro.DESPESA
                ? new ConfirmarPagamentoUseCase(repository).executar(id, data)
                : new ConfirmarRecebimentoUseCase(repository).executar(id, data);
    }
}
