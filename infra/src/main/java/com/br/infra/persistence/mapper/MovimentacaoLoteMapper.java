package com.br.infra.persistence.mapper;

import com.br.core.domain.model.MovimentacaoLote;
import com.br.infra.persistence.entity.MovimentacaoLoteEntity;
import org.springframework.stereotype.Component;
import jakarta.inject.Named;

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
