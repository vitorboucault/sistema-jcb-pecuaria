package com.br.core.domain.model;

import com.br.core.domain.enums.CategoriaDespesa;
import com.br.core.domain.enums.CategoriaReceita;
import com.br.core.domain.enums.SituacaoFinanceira;
import com.br.core.domain.enums.TipoDeCusto;
import com.br.core.domain.enums.TipoLancamentoFinanceiro;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

class ConfirmacaoLancamentoFinanceiroTest {
    private static final LocalDate DATA = LocalDate.of(2025, 1, 10);

    @ParameterizedTest
    @EnumSource(TipoLancamentoFinanceiro.class)
    @DisplayName("@spec:AC-703 Efetivação integral produz cópia imutável preservando todos os outros atributos")
    void efetivaSemModificarOriginal(TipoLancamentoFinanceiro tipo) {
        var original = pendente(tipo);
        var efetivado = confirmar(original, DATA);
        assertThat(efetivado).isNotSameAs(original);
        assertThat(efetivado).usingRecursiveComparison().ignoringFields("situacao", "dataEfetivacao").isEqualTo(original);
        assertThat(efetivado.getValor()).isEqualByComparingTo("100.00");
        assertThat(efetivado.getSituacao()).isEqualTo(SituacaoFinanceira.EFETIVADO);
        assertThat(efetivado.getDataEfetivacao()).isEqualTo(DATA);
        assertThat(original.getSituacao()).isEqualTo(SituacaoFinanceira.PENDENTE);
        assertThat(original.getDataEfetivacao()).isNull();
    }

    @ParameterizedTest
    @EnumSource(TipoLancamentoFinanceiro.class)
    @DisplayName("@spec:AC-704 Mesma data retorna a instância já efetivada")
    void repeteSemAlterar(TipoLancamentoFinanceiro tipo) {
        var efetivado = confirmar(pendente(tipo), DATA);
        assertThat(confirmar(efetivado, DATA)).isSameAs(efetivado);
    }

    @ParameterizedTest
    @EnumSource(TipoLancamentoFinanceiro.class)
    @DisplayName("@spec:AC-704 Data diferente e ação incompatível são conflitos sem alteração")
    void rejeitaConflitos(TipoLancamentoFinanceiro tipo) {
        var pendente = pendente(tipo);
        var efetivado = confirmar(pendente, DATA);
        assertThatThrownBy(() -> confirmar(efetivado, DATA.plusDays(1))).isInstanceOf(IllegalStateException.class);
        assertThatThrownBy(() -> confirmarAcaoIncompativel(pendente)).isInstanceOf(IllegalStateException.class);
        assertThatThrownBy(() -> confirmarAcaoIncompativel(efetivado)).isInstanceOf(IllegalStateException.class);
        assertThat(efetivado.getDataEfetivacao()).isEqualTo(DATA);
        assertThat(pendente.getSituacao()).isEqualTo(SituacaoFinanceira.PENDENTE);
    }

    @ParameterizedTest
    @EnumSource(TipoLancamentoFinanceiro.class)
    @DisplayName("@spec:AC-703 Data nula ou futura não efetiva o lançamento")
    void rejeitaDatasInvalidas(TipoLancamentoFinanceiro tipo) {
        var original = pendente(tipo);
        assertThatThrownBy(() -> confirmar(original, null)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> confirmar(original, LocalDate.now(ZoneId.of("America/Bahia")).plusDays(2)))
                .isInstanceOf(IllegalArgumentException.class);
        assertThat(original.getSituacao()).isEqualTo(SituacaoFinanceira.PENDENTE);
        assertThat(original.getDataEfetivacao()).isNull();
    }

    private LancamentoFinanceiro pendente(TipoLancamentoFinanceiro tipo) {
        return new LancamentoFinanceiro(tipo, "Operação integral", new BigDecimal("100.00"),
                tipo == TipoLancamentoFinanceiro.DESPESA ? CategoriaDespesa.ENERGIA : CategoriaReceita.VENDA_LEITE,
                DATA.minusDays(2), DATA.plusDays(2), SituacaoFinanceira.PENDENTE, null,
                TipoDeCusto.LOTE, UUID.randomUUID());
    }

    private LancamentoFinanceiro confirmar(LancamentoFinanceiro lancamento, LocalDate data) {
        return lancamento.getTipo() == TipoLancamentoFinanceiro.DESPESA
                ? lancamento.confirmarPagamento(data) : lancamento.confirmarRecebimento(data);
    }

    private void confirmarAcaoIncompativel(LancamentoFinanceiro lancamento) {
        if (lancamento.getTipo() == TipoLancamentoFinanceiro.DESPESA) {
            lancamento.confirmarRecebimento(DATA);
        } else {
            lancamento.confirmarPagamento(DATA);
        }
    }
}
