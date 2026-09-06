package com.br.infra.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "venda_animal")
public class VendaAnimalEntity {
        @Id
        private UUID id;

        @Column(name = "animal_id", nullable = false)
        private UUID animalId;

        @Column(name = "data_venda", nullable = false)
        private LocalDate dataVenda;

        @Column(nullable = false, length = 50)
        private String modalidade;

        @Column(name = "peso_vivo_kg", nullable = false)
        private Double pesoVivoKg;

        @Column(name = "rendimento_carcaca_percentual")
        private Double rendimentoCarcacaPercentual;

        @Column(name = "preco_acordado", nullable = false)
        private BigDecimal precoAcordado;

    protected VendaAnimalEntity() {}

    public VendaAnimalEntity(UUID id, UUID animalId, LocalDate dataVenda, String modalidade, Double pesoVivoKg, Double rendimentoCarcacaPercentual, BigDecimal precoAcordado) {
        this.id = id;
        this.animalId = animalId;
        this.dataVenda = dataVenda;
        this.modalidade = modalidade;
        this.pesoVivoKg = pesoVivoKg;
        this.rendimentoCarcacaPercentual = rendimentoCarcacaPercentual;
        this.precoAcordado = precoAcordado;
    }

    public UUID getId() { return id; }
    public UUID getAnimalId() { return animalId; }
    public LocalDate getDataVenda() { return dataVenda; }
    public String getModalidade() { return modalidade; }
    public Double getPesoVivoKg() { return pesoVivoKg; }
    public Double getRendimentoCarcacaPercentual() { return rendimentoCarcacaPercentual; }
    public BigDecimal getPrecoAcordado() { return precoAcordado; }
}
