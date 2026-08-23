package com.sistema.sistemajcb.infrastructure.persistence.mapper;

import com.sistema.sistemajcb.domain.model.Lote;
import com.sistema.sistemajcb.infrastructure.persistence.entity.LoteEntity;
import org.springframework.stereotype.Component;

@Component
public class LoteMapper {
    public LoteEntity toEntity(Lote lote) {
        return new LoteEntity(
                lote.getId(),
                lote.getNome(),
                lote.getFase(),
                lote.getDataFormacao(),
                lote.getDataEncerramento()
        );
    }

    public Lote toDomain(LoteEntity entity) {
       return new Lote(
               entity.getId(),
               entity.getNome(),
               entity.getFase(),
               entity.getDataFormacao(),
               entity.getDataEncerramento()
       );
    }
}
