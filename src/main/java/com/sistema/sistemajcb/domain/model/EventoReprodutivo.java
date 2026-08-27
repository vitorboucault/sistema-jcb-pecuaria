package com.sistema.sistemajcb.domain.model;

import com.sistema.sistemajcb.domain.enums.TipoReproducao;

import java.time.LocalDate;
import java.util.UUID;

public class EventoReprodutivo {

    private UUID id;
    private UUID animalId;
    private UUID estacaoMontaId;
    private TipoReproducao tipoReproducao;
    private LocalDate dataEvento;
    private UUID touroId;

    public EventoReprodutivo(UUID id, UUID animalId, UUID estacaoMontaId, TipoReproducao tipoReproducao, LocalDate dataEvento, UUID touroId) {
        this.id = id;
        this.animalId = animalId;
        this.estacaoMontaId = estacaoMontaId;
        this.tipoReproducao = tipoReproducao;
        this.dataEvento = dataEvento;
        this.touroId = touroId;
    }

    public UUID getId() { return id; }
    public UUID getAnimalId() { return animalId; }
    public UUID getEstacaoMontaId() { return estacaoMontaId; }
    public TipoReproducao getTipoReproducao() { return tipoReproducao; }
    public LocalDate getDataEvento() { return dataEvento; }
    public UUID getTouroId() { return touroId; }
}
