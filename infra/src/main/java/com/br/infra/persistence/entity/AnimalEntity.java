package com.br.infra.persistence.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.UUID;


@Entity
@Table(name = "animal")
public class AnimalEntity {

    @Id
    private UUID id; // Nao usamos @GeneratedValue aqui, pois o UUID já vem criado do Domínio

    @Column(name = "lote_atual_id")
    private UUID loteAtual;

    @Column(name = "mae_id")
    private UUID maeId;

    @Column(name = "brinco_rgd", unique = true, nullable = false, length = 50)
    private String brincoRgd;

    @Column(name = "data_nascimento", nullable = false)
    private LocalDate dataNascimento;

    @Column(nullable = false, length = 10)
    private String sexo;

    @Column(name = "categoria_atual", nullable = false, length = 50)
    private String categoriaAtual;

    @Column(nullable = false, length = 50)
    private String status;

    @Column(name = "data_morte")
    private LocalDate dataMorte;

    protected AnimalEntity() {}

    public AnimalEntity(UUID id, String brincoRgd, UUID loteAtual, LocalDate dataNascimento, String sexo, String categoriaAtual, String status, UUID maeId) {
        this(id, brincoRgd, loteAtual, dataNascimento, sexo, categoriaAtual, status, maeId, null);
    }

    public AnimalEntity(UUID id, String brincoRgd, UUID loteAtual, LocalDate dataNascimento, String sexo, String categoriaAtual, String status, UUID maeId, LocalDate dataMorte) {
        this.id = id;
        this.brincoRgd = brincoRgd;
        this.loteAtual = loteAtual;
        this.dataNascimento = dataNascimento;
        this.sexo = sexo;
        this.categoriaAtual = categoriaAtual;
        this.status = status;
        this.maeId = maeId;
        this.dataMorte = dataMorte;
    }

    public UUID getId() { return id; }
    public String getBrincoRgd() { return brincoRgd; }

    public UUID getLoteAtual() {return loteAtual;}

    public LocalDate getDataNascimento() { return dataNascimento; }
    public String getSexo() { return sexo; }
    public String getCategoriaAtual() { return categoriaAtual; }
    public String getStatus() { return status; }
    public UUID getMaeId() { return maeId; }
    public LocalDate getDataMorte() { return dataMorte; }


}
