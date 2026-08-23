package com.sistema.sistemajcb.domain.model;

import java.time.LocalDate;
import java.util.UUID;

public class Pesagem {

    private final UUID id;
    private final UUID animalId; // Referência solta (Loose Coupling) ao Animal
    private final LocalDate dataPesagem;
    private final double pesoKg;
    private final boolean jejum;

    public Pesagem(UUID animalId, LocalDate dataPesagem, double pesoKg, boolean jejum) {
        if (pesoKg <= 0) {
            throw new IllegalArgumentException("O peso deve ser maior que zero.");
        }
        if (dataPesagem.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("A data da pesagem não pode ser no futuro.");
        }

        this.id = UUID.randomUUID();
        this.animalId = animalId;
        this.dataPesagem = dataPesagem;
        this.pesoKg = pesoKg;
        this.jejum = jejum;
    }

    public Pesagem(UUID id, UUID animalId, LocalDate dataPesagem, double pesoKg, boolean jejum) {
        this.id = id;
        this.animalId = animalId;
        this.dataPesagem = dataPesagem;
        this.pesoKg = pesoKg;
        this.jejum = jejum;
    }

    public UUID getId() { return id; }
    public UUID getAnimalId() { return animalId; }
    public LocalDate getDataPesagem() { return dataPesagem; }
    public double getPesoKg() { return pesoKg; }
    public boolean isJejum() { return jejum; }

}
