package com.br.infra.persistence.mapper;

import com.br.core.domain.enums.StatusPasto;
import com.br.core.domain.model.Pasto;
import com.br.infra.persistence.entity.PastoEntity;
import org.springframework.stereotype.Component;

@Component
public class PastoMapper {
    public PastoEntity toEntity(Pasto pasto) {
        return new PastoEntity(
                pasto.getId(),
                pasto.getNome(),
                pasto.getAreaHectares(),
                pasto.getCapacidadeSuporteUa(),
                pasto.getStatusAtual().name()
        );
    }

    public Pasto toDomain(PastoEntity entity) {
        return new Pasto(
                entity.getId(),
                entity.getNome(),
                entity.getAreaHectares(),
                entity.getCapacidadeSuporteUa(), // Converte String de volta para Enum
                StatusPasto.valueOf(entity.getStatusAtual())
        );
    }
}
