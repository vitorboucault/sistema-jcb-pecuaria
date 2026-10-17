package com.br.usecase.dto;

import com.br.core.domain.enums.SituacaoFinanceira;
import com.br.core.domain.enums.TipoDeCusto;
import com.br.core.domain.enums.TipoLancamentoFinanceiro;
import com.br.core.domain.enums.interfaces.CategoriaFinanceira;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record RegistrarLancamentoFinanceiroCommand(
        TipoLancamentoFinanceiro tipo, String descricao, BigDecimal valor, CategoriaFinanceira categoria,
        LocalDate dataReferencia, LocalDate dataVencimento, SituacaoFinanceira situacao,
        LocalDate dataEfetivacao, TipoDeCusto tipoDeCusto, UUID referenciaId
) {}
