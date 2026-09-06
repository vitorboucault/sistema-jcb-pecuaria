package com.br.infra.persistence.mapper;

import com.br.core.domain.enums.ModalidadeVenda;
import com.br.core.domain.model.VendaAnimal;
import com.br.infra.persistence.entity.VendaAnimalEntity;
import org.springframework.stereotype.Component;

@Component
public class VendaAnimalMapper {
    public VendaAnimalEntity toEntity(VendaAnimal vendaAnimal) {
        return new VendaAnimalEntity(
                vendaAnimal.getId(),
                vendaAnimal.getAnimalId(),
                vendaAnimal.getDataVenda(),
                vendaAnimal.getModalidade().name(),
                vendaAnimal.getPesoVivoKg(),
                vendaAnimal.getRendimentoCarcacaPercentual(),
                vendaAnimal.getPrecoAcordado()
        );
    }
    public VendaAnimal toDomain(VendaAnimalEntity entity) {
        return new VendaAnimal(
                entity.getId(),
                entity.getAnimalId(),
                entity.getDataVenda(),
                ModalidadeVenda.valueOf(entity.getModalidade()),
                entity.getPesoVivoKg(),
                entity.getRendimentoCarcacaPercentual(),
                entity.getPrecoAcordado()
        );
    }
}
