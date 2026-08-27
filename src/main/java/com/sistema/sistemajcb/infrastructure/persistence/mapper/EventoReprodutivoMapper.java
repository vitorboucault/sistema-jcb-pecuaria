package com.sistema.sistemajcb.infrastructure.persistence.mapper;

import com.sistema.sistemajcb.domain.enums.TipoReproducao;
import com.sistema.sistemajcb.domain.model.EventoReprodutivo;
import com.sistema.sistemajcb.infrastructure.persistence.entity.EventoReprodutivoEntity;
import org.springframework.stereotype.Component;

@Component
public class EventoReprodutivoMapper {
    public EventoReprodutivoEntity toEntity(EventoReprodutivo dominio) {
        return new EventoReprodutivoEntity(
                dominio.getId(),
                dominio.getAnimalId(),
                dominio.getEstacaoMontaId(),
                dominio.getTipoReproducao().name(),
                dominio.getDataEvento(),
                dominio.getTouroId()
        );
    }

    public EventoReprodutivo toDomain(EventoReprodutivoEntity entity) {
        return new EventoReprodutivo(
                entity.getId(),
                entity.getAnimalId(),
                entity.getEstacaoMontaId(),
                TipoReproducao.valueOf(entity.getTipoReproducao()),
                entity.getDataEvento(),
                entity.getTouroId()
        );
    }
}
