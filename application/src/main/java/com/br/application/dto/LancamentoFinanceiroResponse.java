package com.br.application.dto;

import com.br.core.domain.enums.CategoriaDespesa;
import com.br.core.domain.enums.CategoriaReceita;
import com.br.core.domain.enums.SituacaoFinanceira;
import com.br.core.domain.enums.TipoDeCusto;
import com.br.core.domain.enums.TipoLancamentoFinanceiro;
import com.br.core.domain.model.LancamentoFinanceiro;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record LancamentoFinanceiroResponse(
        UUID id, TipoLancamentoFinanceiro tipo, String descricao, BigDecimal valor, String categoria,
        LocalDate dataReferencia, LocalDate dataVencimento, SituacaoFinanceira situacao,
        LocalDate dataEfetivacao, TipoDeCusto tipoDeCusto, UUID referenciaId
) {
    public static LancamentoFinanceiroResponse from(LancamentoFinanceiro lancamento) {
        String categoria = lancamento.getCategoria() instanceof CategoriaDespesa despesa
                ? despesa.name() : ((CategoriaReceita) lancamento.getCategoria()).name();
        return new LancamentoFinanceiroResponse(lancamento.getId(), lancamento.getTipo(), lancamento.getDescricao(),
                lancamento.getValor(), categoria, lancamento.getDataReferencia(), lancamento.getDataVencimento(),
                lancamento.getSituacao(), lancamento.getDataEfetivacao(), lancamento.getTipoDeCusto(), lancamento.getReferenciaId());
    }
}
