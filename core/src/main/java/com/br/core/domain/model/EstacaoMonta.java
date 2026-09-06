package com.br.core.domain.model;

import java.time.LocalDate;
import java.util.UUID;

public class EstacaoMonta {
    private UUID id;
    private String nome;
    private LocalDate dataInicio;
    private LocalDate dataFim;
    private String status; // ABERTA, FECHADA

    public EstacaoMonta(UUID id, String nome, LocalDate dataInicio, LocalDate dataFim, String status) {
        if (dataInicio.isAfter(dataFim)) {
            throw new IllegalArgumentException("A data de início nao pode ser depois da data de fim da Estaçao.");
        }
        this.id = id;
        this.nome = nome;
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
        this.status = status;
    }

    public boolean contemData(LocalDate dataEvento) {
        return !dataEvento.isBefore(this.dataInicio) && !dataEvento.isAfter(this.dataFim);
    }

    public boolean isAberta() {
        return "ABERTA".equalsIgnoreCase(this.status);
    }

    public void fecharEstacao() {
        this.status = "FECHADA";
    }

    public UUID getId() { return id; }
    public String getNome() { return nome; }
    public LocalDate getDataInicio() { return dataInicio; }
    public LocalDate getDataFim() { return dataFim; }
    public String getStatus() { return status; }
}