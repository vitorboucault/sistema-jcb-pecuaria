package com.br.infra.persistence.mapper;

import com.br.core.domain.model.AquisicaoAnimal;
import com.br.infra.persistence.entity.AquisicaoAnimalEntity;
import org.springframework.stereotype.Component;

@Component
public class AquisicaoAnimalMapper {

    public AquisicaoAnimalEntity toEntity(AquisicaoAnimal aquisicao) {
        return new AquisicaoAnimalEntity(
                aquisicao.getId(),
                aquisicao.getAnimalId(),
                aquisicao.getDataAquisicao(),
                aquisicao.getValorAquisicao()
        );
    }

    public AquisicaoAnimal toDomain(AquisicaoAnimalEntity entity) {
        return new AquisicaoAnimal(
                entity.getId(),
                entity.getAnimalId(),
                entity.getDataAquisicao(),
                entity.getValorAquisicao()
        );
    }
}
