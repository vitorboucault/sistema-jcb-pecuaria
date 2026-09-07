package com.br.core.domain.model;

import com.br.core.domain.enums.OrigemPesagem;

import java.time.LocalDate;
import java.util.UUID;

public class Pesagem {

    private final UUID id;
    private final UUID animalId; // Referência solta (Loose Coupling) ao Animal
    private final LocalDate dataPesagem;
    private final double pesoKg;
    private final boolean jejum;
    private final OrigemPesagem origem;

    public Pesagem(UUID animalId, LocalDate dataPesagem, double pesoKg, boolean jejum) {
        this(animalId, dataPesagem, pesoKg, jejum, OrigemPesagem.OPERACIONAL);
    }

    public Pesagem(UUID animalId, LocalDate dataPesagem, double pesoKg, boolean jejum, OrigemPesagem origem) {
        if (pesoKg <= 0) {
            throw new IllegalArgumentException("O peso deve ser maior que zero.");
        }
        if (dataPesagem.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("A data da pesagem nao pode ser no futuro.");
        }

        this.id = UUID.randomUUID();
        this.animalId = animalId;
        this.dataPesagem = dataPesagem;
        this.pesoKg = pesoKg;
        this.jejum = jejum;
        if (origem == null) {
            throw new IllegalArgumentException("A origem da pesagem é obrigatória.");
        }
        this.origem = origem;
    }

    public Pesagem(UUID id, UUID animalId, LocalDate dataPesagem, double pesoKg, boolean jejum) {
        this(id, animalId, dataPesagem, pesoKg, jejum, OrigemPesagem.OPERACIONAL);
    }

    public Pesagem(UUID id, UUID animalId, LocalDate dataPesagem, double pesoKg, boolean jejum, OrigemPesagem origem) {
        this.id = id;
        this.animalId = animalId;
        this.dataPesagem = dataPesagem;
        this.pesoKg = pesoKg;
        this.jejum = jejum;
        if (origem == null) {
            throw new IllegalArgumentException("A origem da pesagem é obrigatória.");
        }
        this.origem = origem;
    }

    public UUID getId() { return id; }
    public UUID getAnimalId() { return animalId; }
    public LocalDate getDataPesagem() { return dataPesagem; }
    public double getPeso() { return pesoKg; }
    public boolean isJejum() { return jejum; }
    public OrigemPesagem getOrigem() { return origem; }

}
