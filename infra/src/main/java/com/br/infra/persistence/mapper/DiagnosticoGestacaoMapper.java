package com.br.infra.persistence.mapper;

import com.br.core.domain.enums.ResultadoDiagnostico;
import com.br.core.domain.model.DiagnosticoGestacao;
import com.br.infra.persistence.entity.DiagnosticoGestacaoEntity;
import org.springframework.stereotype.Component;

@Component
public class DiagnosticoGestacaoMapper {
    public DiagnosticoGestacaoEntity toEntity(DiagnosticoGestacao dominio) {
        return new DiagnosticoGestacaoEntity(dominio.getId(), dominio.getAnimalId(), dominio.getEstacaoMontaId(), dominio.getDataDiagnostico(), dominio.getResultado().name(), dominio.getDataProvavelParto());
    }

    public DiagnosticoGestacao toDomain(DiagnosticoGestacaoEntity entity) {
        DiagnosticoGestacao diagnostico = new DiagnosticoGestacao(entity.getId(), entity.getAnimalId(), entity.getEstacaoMontaId(), entity.getDataDiagnostico(), ResultadoDiagnostico.valueOf(entity.getResultado()));
        // Na prática, setaríamos a data do parto via reflexao ou construtor completo para nao disparar a regra de negócio novamente na leitura, mas para o MVP, isso garante o fluxo.
        return diagnostico;
    }
}
