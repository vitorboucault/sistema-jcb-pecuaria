package com.br.application.dto;

import com.br.core.domain.enums.CategoriaDespesa;
import com.br.core.domain.enums.CategoriaReceita;
import com.br.core.domain.enums.SituacaoFinanceira;
import com.br.core.domain.enums.TipoDeCusto;
import com.br.core.domain.enums.TipoLancamentoFinanceiro;
import com.br.core.domain.enums.interfaces.CategoriaFinanceira;
import com.br.usecase.dto.RegistrarLancamentoFinanceiroCommand;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record RegistrarLancamentoFinanceiroRequest(
        @NotNull TipoLancamentoFinanceiro tipo,
        @NotBlank String descricao,
        @NotNull BigDecimal valor,
        @NotBlank String categoria,
        @NotNull LocalDate dataReferencia,
        LocalDate dataVencimento,
        @NotNull SituacaoFinanceira situacao,
        LocalDate dataEfetivacao,
        @NotNull TipoDeCusto tipoDeCusto,
        UUID referenciaId
) {
    public RegistrarLancamentoFinanceiroCommand toCommand() {
        return new RegistrarLancamentoFinanceiroCommand(tipo, descricao, valor, converterCategoria(),
                dataReferencia, dataVencimento, situacao, dataEfetivacao, tipoDeCusto, referenciaId);
    }

    private CategoriaFinanceira converterCategoria() {
        try {
            return switch (tipo) {
                case DESPESA -> CategoriaDespesa.valueOf(categoria);
                case RECEITA -> CategoriaReceita.valueOf(categoria);
            };
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Categoria inválida para o tipo de lançamento.");
        }
    }
}
