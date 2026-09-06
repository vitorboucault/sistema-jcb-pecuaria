package com.br.infra.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "diagnostico_gestacao")
public class DiagnosticoGestacaoEntity {

    @Id
    private UUID id;

    @Column(name = "animal_id", nullable = false)
    private UUID animalId;

    @Column(name = "estacao_monta_id", nullable = false)
    private UUID estacaoMontaId;

    @Column(name = "data_diagnostico", nullable = false)
    private LocalDate dataDiagnostico;

    @Column(nullable = false, length = 50)
    private String resultado;

    @Column(name = "data_provavel_parto")
    private LocalDate dataProvavelParto;

    protected DiagnosticoGestacaoEntity() {}

    public DiagnosticoGestacaoEntity(UUID id, UUID animalId, UUID estacaoMontaId, LocalDate dataDiagnostico, String resultado, LocalDate dataProvavelParto) {
        this.id = id;
        this.animalId = animalId;
        this.estacaoMontaId = estacaoMontaId;
        this.dataDiagnostico = dataDiagnostico;
        this.resultado = resultado;
        this.dataProvavelParto = dataProvavelParto;
    }

    public UUID getId() { return id; }
    public UUID getAnimalId() { return animalId; }
    public String getResultado() { return resultado; }
    public LocalDate getDataProvavelParto() { return dataProvavelParto; }
    public UUID getEstacaoMontaId() {return estacaoMontaId;}
    public LocalDate getDataDiagnostico() {return dataDiagnostico;}
}
