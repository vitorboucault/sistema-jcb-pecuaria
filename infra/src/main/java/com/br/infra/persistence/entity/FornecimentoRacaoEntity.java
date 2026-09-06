package com.br.infra.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "fornecimento_racao")
public class FornecimentoRacaoEntity {

    @Id
    private UUID id;

    @Column(name = "lote_id", nullable = false)
    private UUID loteId;

    @Column(name = "data_fornecimento", nullable = false)
    private LocalDate dataFornecimento;

    @Column(name = "quantidade_kg", nullable = false)
    private Double quantidadeKg;

    @Column(name = "teor_materia_seca", nullable = false)
    private Double teorMateriaSeca;

    public FornecimentoRacaoEntity() {}

    public FornecimentoRacaoEntity(UUID id, UUID loteId, LocalDate dataFornecimento, Double quantidadeKg, Double teorMateriaSeca) {
        this.id = id;
        this.loteId = loteId;
        this.dataFornecimento = dataFornecimento;
        this.quantidadeKg = quantidadeKg;
        this.teorMateriaSeca = teorMateriaSeca;
    }

    public UUID getId() { return id; }
    public UUID getLoteId() { return loteId; }
    public LocalDate getDataFornecimento() { return dataFornecimento; }
    public Double getQuantidadeKg() { return quantidadeKg; }
    public Double getTeorMateriaSeca() { return teorMateriaSeca; }
}