package com.sistema.sistemajcb.domain.model;

import com.sistema.sistemajcb.domain.enums.CategoriaDespesa;
import com.sistema.sistemajcb.domain.enums.TipoDeCusto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public class Despesa {
    private final UUID id;
    private final String descricao;
    private final BigDecimal valor;
    private final LocalDate dataOcorrencia;
    private final CategoriaDespesa categoria;

    private final TipoDeCusto tipoDeCusto;
    private final UUID referenciaId;

    public Despesa(String descricao, BigDecimal valor, LocalDate dataOcorrencia,
                   CategoriaDespesa categoria, TipoDeCusto tipoDeCusto, UUID referenciaId) {

        if (valor == null || valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("O valor da despesa deve ser maior que zero.");
        }
        if (tipoDeCusto != TipoDeCusto.GERAL && referenciaId == null) {
            throw new IllegalArgumentException("Despesas de Lote ou Pasto exigem o ID de referência.");
        }

        this.id = UUID.randomUUID();
        this.descricao = descricao;
        this.valor = valor;
        this.dataOcorrencia = dataOcorrencia;
        this.categoria = categoria;
        this.tipoDeCusto = tipoDeCusto;
        this.referenciaId = referenciaId;
    }

    public Despesa(UUID id, String descricao, BigDecimal valor, LocalDate dataOcorrencia,
                   CategoriaDespesa categoria, TipoDeCusto tipoCentroCusto, UUID referenciaId) {
        this.id = id;
        this.descricao = descricao;
        this.valor = valor;
        this.dataOcorrencia = dataOcorrencia;
        this.categoria = categoria;
        this.tipoDeCusto = tipoCentroCusto;
        this.referenciaId = referenciaId;
    }

    public UUID getId() { return id; }
    public String getDescricao() { return descricao; }
    public BigDecimal getValor() { return valor; }
    public LocalDate getDataOcorrencia() { return dataOcorrencia; }
    public CategoriaDespesa getCategoria() { return categoria; }
    public TipoDeCusto getTipoCentroCusto() { return tipoDeCusto; }
    public UUID getReferenciaId() { return referenciaId; }
}
