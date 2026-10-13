package com.br.core.domain.model.builders;

import com.br.core.domain.model.LancamentoFinanceiro;

import com.br.core.domain.enums.interfaces.CategoriaFinanceira;
import com.br.core.domain.enums.SituacaoFinanceira;
import com.br.core.domain.enums.TipoDeCusto;
import com.br.core.domain.enums.TipoLancamentoFinanceiro;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public final class LancamentoFinanceiroBuilder {

    private UUID id;
    private boolean idInformado;
    private TipoLancamentoFinanceiro tipo;
    private String descricao;
    private BigDecimal valor;
    private CategoriaFinanceira categoria;
    private LocalDate dataReferencia;
    private LocalDate dataVencimento;
    private SituacaoFinanceira situacao;
    private LocalDate dataEfetivacao;
    private TipoDeCusto tipoDeCusto;
    private UUID referenciaId;

    public LancamentoFinanceiroBuilder() {
    }

    public LancamentoFinanceiroBuilder id(UUID id) {
        this.id = id;
        this.idInformado = true;
        return this;
    }

    public LancamentoFinanceiroBuilder tipo(TipoLancamentoFinanceiro tipo) {
        this.tipo = tipo;
        return this;
    }

    public LancamentoFinanceiroBuilder descricao(String descricao) {
        this.descricao = descricao;
        return this;
    }

    public LancamentoFinanceiroBuilder valor(BigDecimal valor) {
        this.valor = valor;
        return this;
    }

    public LancamentoFinanceiroBuilder categoria(CategoriaFinanceira categoria) {
        this.categoria = categoria;
        return this;
    }

    public LancamentoFinanceiroBuilder dataReferencia(LocalDate dataReferencia) {
        this.dataReferencia = dataReferencia;
        return this;
    }

    public LancamentoFinanceiroBuilder dataVencimento(LocalDate dataVencimento) {
        this.dataVencimento = dataVencimento;
        return this;
    }

    public LancamentoFinanceiroBuilder situacao(SituacaoFinanceira situacao) {
        this.situacao = situacao;
        return this;
    }

    public LancamentoFinanceiroBuilder dataEfetivacao(LocalDate dataEfetivacao) {
        this.dataEfetivacao = dataEfetivacao;
        return this;
    }

    public LancamentoFinanceiroBuilder tipoDeCusto(TipoDeCusto tipoDeCusto) {
        this.tipoDeCusto = tipoDeCusto;
        return this;
    }

    public LancamentoFinanceiroBuilder referenciaId(UUID referenciaId) {
        this.referenciaId = referenciaId;
        return this;
    }

    public LancamentoFinanceiro build() {
        return new LancamentoFinanceiro(idInformado ? id : UUID.randomUUID(), tipo, descricao,
                valor, categoria, dataReferencia, dataVencimento, situacao, dataEfetivacao,
                tipoDeCusto, referenciaId);
    }
}
