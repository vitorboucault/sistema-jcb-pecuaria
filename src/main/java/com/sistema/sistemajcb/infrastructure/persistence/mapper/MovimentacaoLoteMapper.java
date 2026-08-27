package com.sistema.sistemajcb.infrastructure.persistence.mapper;

import com.sistema.sistemajcb.domain.model.MovimentacaoLote;
import com.sistema.sistemajcb.infrastructure.persistence.entity.MovimentacaoLoteEntity;
import org.springframework.stereotype.Component;

@Component
public class MovimentacaoLoteMapper {
    public MovimentacaoLoteEntity toEntity(MovimentacaoLote dominio) {
        return new MovimentacaoLoteEntity(
                dominio.getId(),
                dominio.getLoteId(),
                dominio.getPastoId(),
                dominio.getDataEntrada(),
                dominio.getDataSaida()
        );
    }

    public MovimentacaoLote toDomain(MovimentacaoLoteEntity entity) {
        return new MovimentacaoLote(
                entity.getId(),
                entity.getLoteId(),
                entity.getPastoId(),
                entity.getDataEntrada(),
                entity.getDataSaida()
        );
    }

}
