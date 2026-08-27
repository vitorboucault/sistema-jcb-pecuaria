package com.sistema.sistemajcb.domain.model;

import java.time.LocalDate;
import java.util.UUID;

public class MovimentacaoLote {
    private UUID id;
    private UUID loteId;
    private UUID pastoId;
    private LocalDate dataEntrada;
    private LocalDate dataSaida;

    public MovimentacaoLote(UUID id, UUID loteId, UUID pastoId, LocalDate dataEntrada, LocalDate dataSaida) {
        if (dataSaida != null && dataSaida.isBefore(dataEntrada)) {
            throw new IllegalArgumentException("A data de saída não pode ser anterior à data de entrada.");
        }
        this.id = id;
        this.loteId = loteId;
        this.pastoId = pastoId;
        this.dataEntrada = dataEntrada;
        this.dataSaida = dataSaida;
    }

    public boolean isNoPasto() {
        return this.dataSaida == null;
    }

    public void registrarSaida(LocalDate data) {
        if (data.isBefore(this.dataEntrada)) {
            throw new IllegalArgumentException("Data de saída inválida.");
        }
        this.dataSaida = data;
    }

    public UUID getId() {return id;}

    public UUID getLoteId() {return loteId;}

    public UUID getPastoId() {return pastoId;}

    public LocalDate getDataEntrada() {return dataEntrada;}

    public LocalDate getDataSaida() {return dataSaida;}
}
