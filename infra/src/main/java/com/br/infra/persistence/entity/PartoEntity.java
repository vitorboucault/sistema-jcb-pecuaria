package com.br.infra.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "parto")
public class PartoEntity {

    @Id
    private UUID id;
    @Column(name = "matriz_id", nullable = false)
    private UUID matrizId;
    @Column(name = "bezerro_id")
    private UUID bezerroId;
    @Column(name = "estacao_monta_id", nullable = false)
    private UUID estacaoMontaId;
    @Column(name = "data_parto", nullable = false)
    private LocalDate dataParto;
    @Column(nullable = false, length = 50)
    private String tipo;
    @Column(nullable = false, length = 50)
    private String condicao;
    @Column(name = "peso_nascimento_kg")
    private Double pesoNascimentoKg;

    protected PartoEntity() {}

    public PartoEntity(UUID id, UUID matrizId, UUID bezerroId, UUID estacaoMontaId, LocalDate dataParto, String tipo, String condicao, Double pesoNascimentoKg) {
        this.id = id;
        this.matrizId = matrizId;
        this.bezerroId = bezerroId;
        this.estacaoMontaId = estacaoMontaId;
        this.dataParto = dataParto;
        this.tipo = tipo;
        this.condicao = condicao;
        this.pesoNascimentoKg = pesoNascimentoKg;
    }

    public UUID getId() { return id; }
    public UUID getMatrizId() {return matrizId;}
    public UUID getBezerroId() {return bezerroId;}
    public UUID getEstacaoMontaId() {return estacaoMontaId;}
    public LocalDate getDataParto() {return dataParto;}
    public String getTipo() {return tipo;}
    public String getCondicao() {return condicao;}
    public Double getPesoNascimentoKg() {return pesoNascimentoKg;}
}
