package com.sistema.sistemajcb.infrastructure.persistence.mapper;

import com.sistema.sistemajcb.domain.model.Desmame;
import com.sistema.sistemajcb.infrastructure.persistence.entity.DesmameEntity;
import org.springframework.stereotype.Component;

@Component
public class DesmameMapper {
    public DesmameEntity toEntity(Desmame dominio) {
        return new DesmameEntity(
                dominio.getId(), dominio.getBezerroId(), dominio.getEstacaoMontaId(),
                dominio.getDataNascimento(), dominio.getDataDesmame(),
                dominio.getPesoNascimento(), dominio.getPesoDesmame()
        );
    }
}
