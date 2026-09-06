package com.br.core.domain.model;

import com.br.core.domain.enums.ResultadoDiagnostico;

import java.time.LocalDate;
import java.util.UUID;

public class DiagnosticoGestacao {
    private UUID id;
    private UUID animalId;
    private UUID estacaoMontaId;
    private LocalDate dataDiagnostico;
    private ResultadoDiagnostico resultado;
    private LocalDate dataProvavelParto;

    public DiagnosticoGestacao(UUID id, UUID animalId, UUID estacaoMontaId, LocalDate dataDiagnostico, ResultadoDiagnostico resultado) {
        this.id = id;
        this.animalId = animalId;
        this.estacaoMontaId = estacaoMontaId;
        this.dataDiagnostico = dataDiagnostico;
        this.resultado = resultado;
    }

    public void projetarParto(LocalDate dataInseminacao) {
        if (this.resultado == ResultadoDiagnostico.PRENHE && dataInseminacao != null) {
            this.dataProvavelParto = dataInseminacao.plusDays(295);
        } else {
            this.dataProvavelParto = null;
        }
    }
    public ResultadoDiagnostico getResultado() { return resultado; }
    public LocalDate getDataProvavelParto() { return dataProvavelParto; }
    public UUID getId() {return id;}
    public UUID getAnimalId() {return animalId;}
    public UUID getEstacaoMontaId() {return estacaoMontaId;}
    public LocalDate getDataDiagnostico() {return dataDiagnostico;}
}
