package com.br.infra.persistence.mapper;

import com.br.core.domain.model.Animal;
import com.br.core.domain.enums.Categoria;
import com.br.core.domain.enums.Sexo;
import com.br.core.domain.enums.Status;
import com.br.infra.persistence.entity.AnimalEntity;
import org.springframework.stereotype.Component;

@Component
public class AnimalMapper {
    public AnimalEntity toEntity(Animal animal) {
        return new AnimalEntity(
                animal.getId(),
                animal.getBrincoRgd(),
                animal.getLoteId(),
                animal.getDataNascimento(),
                animal.getSexo().name(), // Grava o Enum como String
                animal.getCategoriaAtual().name(),
                animal.getStatus().name(),
                animal.getMaeId(),
                animal.getDataMorte()
        );
    }

    public Animal toDomain(AnimalEntity entity) {
        return new Animal(
                entity.getId(),
                entity.getBrincoRgd(),
                entity.getDataNascimento(),
                Sexo.valueOf(entity.getSexo()), // Converte String de volta para Enum
                Categoria.valueOf(entity.getCategoriaAtual()),
                Status.valueOf(entity.getStatus()),
                entity.getMaeId(),
                entity.getLoteAtual(),
                entity.getDataMorte()
        );
    }
}
