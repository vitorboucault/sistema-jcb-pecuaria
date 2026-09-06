package com.br.infra.persistence.mapper;

import com.br.core.domain.model.Lote;
import com.br.infra.persistence.entity.LoteEntity;
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
