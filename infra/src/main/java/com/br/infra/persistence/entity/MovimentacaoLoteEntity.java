package com.br.infra.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "movimentacao_lote")
public class MovimentacaoLoteEntity {

    @Id
    private UUID id;

    @Column(name = "lote_id", nullable = false)
    private UUID loteId;

    @Column(name = "pasto_id", nullable = false)
    private UUID pastoId;

    @Column(name = "data_entrada", nullable = false)
    private LocalDate dataEntrada;

    @Column(name = "data_saida")
    private LocalDate dataSaida;

    protected MovimentacaoLoteEntity() {}

    public MovimentacaoLoteEntity(UUID id, UUID loteId, UUID pastoId, LocalDate dataEntrada, LocalDate dataSaida) {
        this.id = id;
        this.loteId = loteId;
        this.pastoId = pastoId;
        this.dataEntrada = dataEntrada;
        this.dataSaida = dataSaida;
    }

    public UUID getId() { return id; }
    public UUID getLoteId() { return loteId; }
    public UUID getPastoId() { return pastoId; }
    public LocalDate getDataEntrada() { return dataEntrada; }
    public LocalDate getDataSaida() { return dataSaida; }
}
