package com.sistema.sistemajcb.infrastructure.persistence.entity;

import com.sistema.sistemajcb.domain.enums.StatusPasto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "pasto")
public class PastoEntity {

    @Id
    private UUID id; // Não usamos @GeneratedValue aqui, pois o UUID já vem criado do Domínio

    @Column(name = "nome", nullable = false, length = 100)
    private String nome;

    @Column(name = "area_hectares", nullable = false)
    private Double areaHectares;

    @Column(name = "capacidade_suporte_ua")
    private Double capacidadeSuporteUa;

    @Column(name = "status_atual", nullable = false, length = 50)
    private String statusAtual;

    protected PastoEntity() {}

    public PastoEntity(UUID id, String nome, Double areaHectares, Double capacidadeSuporteUa, String statusAtual) {
        this.id = id;
        this.nome = nome;
        this.areaHectares = areaHectares;
        this.capacidadeSuporteUa = capacidadeSuporteUa;
        this.statusAtual = statusAtual;
    }

    public UUID getId() {return id;}
    public String getNome() {return nome;}
    public Double getAreaHectares() {return areaHectares;}
    public Double getCapacidadeSuporteUa() {return capacidadeSuporteUa;}
    public String getStatusAtual() {return statusAtual;}
}
