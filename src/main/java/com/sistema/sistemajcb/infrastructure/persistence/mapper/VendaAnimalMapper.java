package com.sistema.sistemajcb.infrastructure.persistence.mapper;

import com.sistema.sistemajcb.domain.enums.ModalidadeVenda;
import com.sistema.sistemajcb.domain.model.VendaAnimal;
import com.sistema.sistemajcb.infrastructure.persistence.entity.VendaAnimalEntity;

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
