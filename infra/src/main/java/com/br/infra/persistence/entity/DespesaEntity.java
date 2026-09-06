package com.br.infra.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "transacao_financeira")
public class DespesaEntity {

    @Id
    private UUID id;

    @Column(nullable = false, length = 20)
    private String tipo;

    @Column(name = "data_transacao", nullable = false)
    private LocalDate dataTransacao;

    @Column(nullable = false)
    private BigDecimal valor;

    @Column(nullable = false, length = 50)
    private String categoria;
    @Column(name = "centro_custo_id")
    private UUID centroCustoId;
    @Column(name = "tipo_centro_custo", length = 50)
    private String tipoCentroCusto;

    protected DespesaEntity() {}

    public DespesaEntity(UUID id, String tipo, LocalDate dataTransacao, BigDecimal valor, String categoria, UUID centroCustoId, String tipoCentroCusto) {
        this.id = id;
        this.tipo = tipo;
        this.dataTransacao = dataTransacao;
        this.valor = valor;
        this.categoria = categoria;
        this.centroCustoId = centroCustoId;
        this.tipoCentroCusto = tipoCentroCusto;
    }

    public UUID getId() { return id; }
    public String getTipo() { return tipo; }
    public LocalDate getDataTransacao() { return dataTransacao; }
    public BigDecimal getValor() { return valor; }
    public String getCategoria() { return categoria; }
    public UUID getCentroCustoId() { return centroCustoId; }
    public String getTipoCentroCusto() { return tipoCentroCusto; }
}
