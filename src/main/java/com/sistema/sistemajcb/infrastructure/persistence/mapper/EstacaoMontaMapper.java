package com.sistema.sistemajcb.infrastructure.persistence.mapper;

import com.sistema.sistemajcb.domain.model.EstacaoMonta;
import com.sistema.sistemajcb.infrastructure.persistence.entity.EstacaoMontaEntity;
import org.springframework.stereotype.Component;

@Component
public class EstacaoMontaMapper {
    public EstacaoMontaEntity toEntity(EstacaoMonta dominio) {
        return new EstacaoMontaEntity(dominio.getId(), dominio.getNome(), dominio.getDataInicio(), dominio.getDataFim(), dominio.getStatus());
    }

    public EstacaoMonta toDomain(EstacaoMontaEntity entity) {
        return new EstacaoMonta(entity.getId(), entity.getNome(), entity.getDataInicio(), entity.getDataFim(), entity.getStatus());
    }
}
