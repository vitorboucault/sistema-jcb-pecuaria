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

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal valor;

    @Column(nullable = false, length = 50)
    private String categoria;
    @Column(name = "centro_custo_id")
    private UUID centroCustoId;
    @Column(name = "tipo_centro_custo", nullable = false, length = 50)
    private String tipoCentroCusto;

    @Column(nullable = false, columnDefinition = "text")
    private String descricao;
    @Column(nullable = false, length = 20)
    private String situacao;
    @Column(name = "data_vencimento")
    private LocalDate dataVencimento;
    @Column(name = "data_efetivacao")
    private LocalDate dataEfetivacao;
    @Column(name = "origem_tipo", length = 30)
    private String origemTipo;
    @Column(name = "origem_id")
    private UUID origemId;

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
    public String getDescricao() { return descricao; }
    public String getSituacao() { return situacao; }
    public LocalDate getDataVencimento() { return dataVencimento; }
    public LocalDate getDataEfetivacao() { return dataEfetivacao; }
    public String getOrigemTipo() { return origemTipo; }
    public UUID getOrigemId() { return origemId; }

    public void definirDadosFinanceiros(String descricao, String situacao, LocalDate dataVencimento,
                                        LocalDate dataEfetivacao) {
        this.descricao = descricao;
        this.situacao = situacao;
        this.dataVencimento = dataVencimento;
        this.dataEfetivacao = dataEfetivacao;
    }

    public void definirOrigem(String origemTipo, UUID origemId) {
        this.origemTipo = origemTipo;
        this.origemId = origemId;
    }

    public void atualizarDadosDespesa(LocalDate data, BigDecimal valor, String categoria,
                                     UUID centroCustoId, String tipoCentroCusto) {
        this.dataTransacao = data;
        this.valor = valor;
        this.categoria = categoria;
        this.centroCustoId = centroCustoId;
        this.tipoCentroCusto = tipoCentroCusto;
    }
}
