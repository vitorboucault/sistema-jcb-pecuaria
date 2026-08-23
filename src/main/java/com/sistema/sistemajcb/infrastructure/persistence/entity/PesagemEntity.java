package com.sistema.sistemajcb.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "pesagem")
public class PesagemEntity {
    @Id
    private UUID id;
    @Column(name = "animal_id", nullable = false)
    private UUID animalId;
    @Column(name = "data_pesagem", nullable = false)
    private LocalDate dataPesagem;
    @Column(name = "peso_kg", nullable = false)
    private Double pesoKg;

    protected PesagemEntity() {}

    public PesagemEntity(UUID id, UUID animalId, LocalDate dataPesagem, Double pesoKg) {
        this.id = id;
        this.animalId = animalId;
        this.dataPesagem = dataPesagem;
        this.pesoKg = pesoKg;
    }

    public UUID getId() { return id; }
    public UUID getAnimalId() { return animalId; }
    public LocalDate getDataPesagem() { return dataPesagem; }
    public Double getPesoKg() { return pesoKg; }
}
