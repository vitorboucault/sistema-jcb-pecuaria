package com.br.core.domain.model;

import com.br.core.domain.model.builders.LancamentoFinanceiroBuilder;
import com.br.core.domain.enums.CategoriaDespesa;
import com.br.core.domain.enums.CategoriaReceita;
import com.br.core.domain.enums.SituacaoFinanceira;
import com.br.core.domain.enums.TipoDeCusto;
import com.br.core.domain.enums.TipoLancamentoFinanceiro;
import com.br.core.domain.enums.interfaces.CategoriaFinanceira;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.UUID;

public final class LancamentoFinanceiro {

    private static final BigDecimal VALOR_MAXIMO = new BigDecimal("9999999999.99");

    private final UUID id;
    private final TipoLancamentoFinanceiro tipo;
    private final String descricao;
    private final BigDecimal valor;
    private final CategoriaFinanceira categoria;
    private final LocalDate dataReferencia;
    private final LocalDate dataVencimento;
    private final SituacaoFinanceira situacao;
    private final LocalDate dataEfetivacao;
    private final TipoDeCusto tipoDeCusto;
    private final UUID referenciaId;

    public LancamentoFinanceiro(
            TipoLancamentoFinanceiro tipo, String descricao, BigDecimal valor,
            CategoriaFinanceira categoria, LocalDate dataReferencia, LocalDate dataVencimento,
            SituacaoFinanceira situacao, LocalDate dataEfetivacao,
            TipoDeCusto tipoDeCusto, UUID referenciaId
    ) {
        this(UUID.randomUUID(), tipo, descricao, valor, categoria, dataReferencia, dataVencimento,
                situacao, dataEfetivacao, tipoDeCusto, referenciaId);
    }

    public LancamentoFinanceiro(
            UUID id, TipoLancamentoFinanceiro tipo, String descricao, BigDecimal valor,
            CategoriaFinanceira categoria, LocalDate dataReferencia, LocalDate dataVencimento,
            SituacaoFinanceira situacao, LocalDate dataEfetivacao,
            TipoDeCusto tipoDeCusto, UUID referenciaId
    ) {
        this(id, tipo, descricao, valor, categoria, dataReferencia, dataVencimento,
                situacao, dataEfetivacao, tipoDeCusto, referenciaId,
                Clock.system(ZoneId.of("America/Bahia")));
    }

    LancamentoFinanceiro(
            UUID id, TipoLancamentoFinanceiro tipo, String descricao, BigDecimal valor,
            CategoriaFinanceira categoria, LocalDate dataReferencia, LocalDate dataVencimento,
            SituacaoFinanceira situacao, LocalDate dataEfetivacao,
            TipoDeCusto tipoDeCusto, UUID referenciaId, Clock clock
    ) {
        validarIdentificacao(id, tipo);
        String descricaoNormalizada = normalizarDescricao(descricao);
        BigDecimal valorNormalizado = validarValor(valor);
        validarCategoria(tipo, categoria);
        validarDatasESituacao(dataReferencia, situacao, dataEfetivacao, clock);
        validarCentroDeCusto(tipoDeCusto, referenciaId);

        this.id = id;
        this.tipo = tipo;
        this.descricao = descricaoNormalizada;
        this.valor = valorNormalizado;
        this.categoria = categoria;
        this.dataReferencia = dataReferencia;
        this.dataVencimento = dataVencimento;
        this.situacao = situacao;
        this.dataEfetivacao = dataEfetivacao;
        this.tipoDeCusto = tipoDeCusto;
        this.referenciaId = referenciaId;
    }

    private static void validarIdentificacao(UUID id, TipoLancamentoFinanceiro tipo) {
        if (id == null) {
            throw new IllegalArgumentException("O ID do lançamento é obrigatório.");
        }
        if (tipo == null) {
            throw new IllegalArgumentException("O tipo do lançamento é obrigatório.");
        }
    }

    private static String normalizarDescricao(String descricao) {
        if (descricao == null || descricao.isBlank()) {
            throw new IllegalArgumentException("A descrição do lançamento é obrigatória.");
        }
        return descricao.strip();
    }

    private static BigDecimal validarValor(BigDecimal valor) {
        if (valor == null || valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("O valor do lançamento deve ser maior que zero.");
        }
        if (valor.compareTo(VALOR_MAXIMO) > 0) {
            throw new IllegalArgumentException("O valor do lançamento não pode exceder 9.999.999.999,99.");
        }
        try {
            return valor.setScale(2, RoundingMode.UNNECESSARY);
        } catch (ArithmeticException exception) {
            throw new IllegalArgumentException("O valor do lançamento não pode ter frações de centavo.");
        }
    }

    private static void validarCategoria(TipoLancamentoFinanceiro tipo, CategoriaFinanceira categoria) {
        if (categoria == null) {
            throw new IllegalArgumentException("A categoria do lançamento é obrigatória.");
        }
        if ((tipo == TipoLancamentoFinanceiro.RECEITA && !(categoria instanceof CategoriaReceita))
                || (tipo == TipoLancamentoFinanceiro.DESPESA && !(categoria instanceof CategoriaDespesa))) {
            throw new IllegalArgumentException("A categoria deve ser compatível com o tipo do lançamento.");
        }
    }

    private static void validarDatasESituacao(
            LocalDate dataReferencia, SituacaoFinanceira situacao, LocalDate dataEfetivacao, Clock clock
    ) {
        if (dataReferencia == null) {
            throw new IllegalArgumentException("A data de referência é obrigatória.");
        }
        if (situacao == null) {
            throw new IllegalArgumentException("A situação financeira é obrigatória.");
        }
        if (situacao == SituacaoFinanceira.PENDENTE && dataEfetivacao != null) {
            throw new IllegalArgumentException("Lançamento pendente não pode ter data de efetivação.");
        }
        if (situacao == SituacaoFinanceira.EFETIVADO && dataEfetivacao == null) {
            throw new IllegalArgumentException("Lançamento efetivado exige data de efetivação.");
        }
        if (dataEfetivacao != null && dataEfetivacao.isAfter(LocalDate.now(clock))) {
            throw new IllegalArgumentException("A data de efetivação não pode ser futura.");
        }
    }

    private static void validarCentroDeCusto(TipoDeCusto tipoDeCusto, UUID referenciaId) {
        if (tipoDeCusto == null) {
            throw new IllegalArgumentException("O tipo de custo é obrigatório.");
        }
        if (tipoDeCusto == TipoDeCusto.GERAL && referenciaId != null) {
            throw new IllegalArgumentException("Centro de custo GERAL não pode ter referência.");
        }
        if (tipoDeCusto != TipoDeCusto.GERAL && referenciaId == null) {
            throw new IllegalArgumentException("Centro de custo ANIMAL, LOTE ou PASTO exige referência.");
        }

    }

    public static LancamentoFinanceiroBuilder builder() {
        return new LancamentoFinanceiroBuilder();
    }

    public LancamentoFinanceiro confirmarPagamento(LocalDate dataEfetivacao) {
        return efetivar(TipoLancamentoFinanceiro.DESPESA, dataEfetivacao);
    }

    public LancamentoFinanceiro confirmarRecebimento(LocalDate dataEfetivacao) {
        return efetivar(TipoLancamentoFinanceiro.RECEITA, dataEfetivacao);
    }

    private LancamentoFinanceiro efetivar(TipoLancamentoFinanceiro tipoEsperado, LocalDate data) {
        if (tipo != tipoEsperado) {
            throw new IllegalStateException("A ação de confirmação é incompatível com o tipo do lançamento.");
        }
        validarDatasESituacao(dataReferencia, SituacaoFinanceira.EFETIVADO, data,
                Clock.system(ZoneId.of("America/Bahia")));
        if (situacao == SituacaoFinanceira.EFETIVADO) {
            if (!dataEfetivacao.equals(data)) {
                throw new IllegalStateException("O lançamento já foi efetivado em outra data.");
            }
            return this;
        }
        return new LancamentoFinanceiro(id, tipo, descricao, valor, categoria, dataReferencia,
                dataVencimento, SituacaoFinanceira.EFETIVADO, data, tipoDeCusto, referenciaId);
    }

    public UUID getId() { return id; }
    public TipoLancamentoFinanceiro getTipo() { return tipo; }
    public String getDescricao() { return descricao; }
    public BigDecimal getValor() { return valor; }
    public CategoriaFinanceira getCategoria() { return categoria; }
    public LocalDate getDataReferencia() { return dataReferencia; }
    public LocalDate getDataVencimento() { return dataVencimento; }
    public SituacaoFinanceira getSituacao() { return situacao; }
    public LocalDate getDataEfetivacao() { return dataEfetivacao; }
    public TipoDeCusto getTipoDeCusto() { return tipoDeCusto; }
    public UUID getReferenciaId() { return referenciaId; }
}
