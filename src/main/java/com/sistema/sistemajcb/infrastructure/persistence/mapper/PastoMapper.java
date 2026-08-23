package com.sistema.sistemajcb.infrastructure.persistence.mapper;

import com.sistema.sistemajcb.domain.Animal;
import com.sistema.sistemajcb.domain.enums.Categoria;
import com.sistema.sistemajcb.domain.enums.StatusPasto;
import com.sistema.sistemajcb.domain.model.Pasto;
import com.sistema.sistemajcb.infrastructure.persistence.entity.AnimalEntity;
import com.sistema.sistemajcb.infrastructure.persistence.entity.PastoEntity;
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
