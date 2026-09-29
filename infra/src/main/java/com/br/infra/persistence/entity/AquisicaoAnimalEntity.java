package com.br.infra.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(
        name = "aquisicao_animal",
        uniqueConstraints = @UniqueConstraint(
                name = "uq_aquisicao_animal_animal",
                columnNames = "animal_id"
        )
)
public class AquisicaoAnimalEntity {

    @Id
    private UUID id;

    @Column(name = "animal_id", nullable = false)
    private UUID animalId;

    @Column(name = "data_aquisicao")
    private LocalDate dataAquisicao;

    @Column(name = "valor_aquisicao", precision = 12, scale = 2)
    private BigDecimal valorAquisicao;

    protected AquisicaoAnimalEntity() {
    }

    public AquisicaoAnimalEntity(
            UUID id,
            UUID animalId,
            LocalDate dataAquisicao,
            BigDecimal valorAquisicao
    ) {
        this.id = id;
        this.animalId = animalId;
        this.dataAquisicao = dataAquisicao;
        this.valorAquisicao = valorAquisicao;
    }

    public UUID getId() {
        return id;
    }

    public UUID getAnimalId() {
        return animalId;
    }

    public LocalDate getDataAquisicao() {
        return dataAquisicao;
    }

    public BigDecimal getValorAquisicao() {
        return valorAquisicao;
    }
}
