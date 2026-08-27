package com.sistema.sistemajcb.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "desmame")
public class DesmameEntity {

    @Id
    private UUID id;
    @Column(name = "bezerro_id", nullable = false)
    private UUID bezerroId;
    @Column(name = "estacao_monta_id", nullable = false)
    private UUID estacaoMontaId;
    @Column(name = "data_nascimento", nullable = false)
    private LocalDate dataNascimento;
    @Column(name = "data_desmame", nullable = false)
    private LocalDate dataDesmame;
    @Column(name = "peso_nascimento", nullable = false)
    private Double pesoNascimento;
    @Column(name = "peso_desmame", nullable = false)
    private Double pesoDesmame;

    protected DesmameEntity() {}

    public DesmameEntity(UUID id, UUID bezerroId, UUID estacaoMontaId, LocalDate dataNascimento, LocalDate dataDesmame, Double pesoNascimento, Double pesoDesmame) {
        this.id = id;
        this.bezerroId = bezerroId;
        this.estacaoMontaId = estacaoMontaId;
        this.dataNascimento = dataNascimento;
        this.dataDesmame = dataDesmame;
        this.pesoNascimento = pesoNascimento;
        this.pesoDesmame = pesoDesmame;
    }

    public UUID getId() { return id;}
    public UUID getBezerroId() {return bezerroId;}
    public UUID getEstacaoMontaId() {return estacaoMontaId;}
    public LocalDate getDataNascimento() {return dataNascimento;}
    public LocalDate getDataDesmame() {return dataDesmame;}
    public Double getPesoNascimento() {return pesoNascimento;}
    public Double getPesoDesmame() {return pesoDesmame;}
}
