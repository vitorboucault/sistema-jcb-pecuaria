package com.sistema.sistemajcb.infrastructure.persistence.mapper;

import com.sistema.sistemajcb.domain.enums.ResultadoDiagnostico;
import com.sistema.sistemajcb.domain.model.DiagnosticoGestacao;
import com.sistema.sistemajcb.infrastructure.persistence.entity.DiagnosticoGestacaoEntity;
import org.springframework.stereotype.Component;

@Component
public class DiagnosticoGestacaoMapper {
    public DiagnosticoGestacaoEntity toEntity(DiagnosticoGestacao dominio) {
        return new DiagnosticoGestacaoEntity(dominio.getId(), dominio.getAnimalId(), dominio.getEstacaoMontaId(), dominio.getDataDiagnostico(), dominio.getResultado().name(), dominio.getDataProvavelParto());
    }

    public DiagnosticoGestacao toDomain(DiagnosticoGestacaoEntity entity) {
        DiagnosticoGestacao diagnostico = new DiagnosticoGestacao(entity.getId(), entity.getAnimalId(), entity.getEstacaoMontaId(), entity.getDataDiagnostico(), ResultadoDiagnostico.valueOf(entity.getResultado()));
        // Na prática, setaríamos a data do parto via reflexão ou construtor completo para não disparar a regra de negócio novamente na leitura, mas para o MVP, isso garante o fluxo.
        return diagnostico;
    }
}
