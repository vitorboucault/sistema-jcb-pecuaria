package com.br.infra.persistence.entity;

import com.br.core.domain.enums.OrigemPesagem;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;

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
    private Double peso;

    @Column(name = "origem", nullable = false, length = 30)
    private String origem;

    protected PesagemEntity() {}

    public PesagemEntity(UUID id, UUID animalId, LocalDate dataPesagem, Double pesoKg) {
        this(id, animalId, dataPesagem, pesoKg, OrigemPesagem.OPERACIONAL);
    }

    public PesagemEntity(UUID id, UUID animalId, LocalDate dataPesagem, Double pesoKg, OrigemPesagem origem) {
        this.id = id;
        this.animalId = animalId;
        this.dataPesagem = dataPesagem;
        this.peso = pesoKg;
        this.origem = origem.name();
    }

    public UUID getId() {return id;}
    public UUID getAnimalId() {return animalId;}
    public LocalDate getDataPesagem() {
        return dataPesagem;
    }

    public Double getPeso() {
        return peso;
    }

    public OrigemPesagem getOrigem() {
        return OrigemPesagem.valueOf(origem);
    }
}
