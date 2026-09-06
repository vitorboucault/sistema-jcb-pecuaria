package com.br.infra.persistence.mapper;

import com.br.core.domain.model.Desmame;
import com.br.infra.persistence.entity.DesmameEntity;
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
