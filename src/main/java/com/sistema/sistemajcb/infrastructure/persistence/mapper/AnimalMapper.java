package com.sistema.sistemajcb.infrastructure.persistence.mapper;

import com.sistema.sistemajcb.domain.Animal;
import com.sistema.sistemajcb.domain.enums.Categoria;
import com.sistema.sistemajcb.domain.enums.Sexo;
import com.sistema.sistemajcb.domain.enums.Status;
import com.sistema.sistemajcb.infrastructure.persistence.entity.AnimalEntity;
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
                animal.getMaeId()
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
                entity.getLoteAtual()
        );
    }
}
