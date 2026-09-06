package com.br.infra.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "evento_reprodutivo")
public class EventoReprodutivoEntity {

    @Id
    private UUID id;

    @Column(name = "animal_id", nullable = false)
    private UUID animalId;

    @Column(name = "estacao_monta_id", nullable = false)
    private UUID estacaoMontaId;

    @Column(name = "tipo_reproducao", nullable = false, length = 50)
    private String tipoReproducao; // Salva o nome do Enum

    @Column(name = "data_evento", nullable = false)
    private LocalDate dataEvento;

    @Column(name = "touro_id")
    private UUID touroId;

    protected EventoReprodutivoEntity() {}

    public EventoReprodutivoEntity(UUID id, UUID animalId, UUID estacaoMontaId, String tipoReproducao, LocalDate dataEvento, UUID touroId) {
        this.id = id;
        this.animalId = animalId;
        this.estacaoMontaId = estacaoMontaId;
        this.tipoReproducao = tipoReproducao;
        this.dataEvento = dataEvento;
        this.touroId = touroId;
    }

    public UUID getId() { return id; }
    public UUID getAnimalId() {return animalId;}
    public UUID getEstacaoMontaId() {return estacaoMontaId;}
    public String getTipoReproducao() {return tipoReproducao;}
    public LocalDate getDataEvento() {return dataEvento;}
    public UUID getTouroId() {return touroId;}
}
