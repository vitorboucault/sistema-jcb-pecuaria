package com.br.infra.persistence.repository;

import com.br.infra.persistence.entity.AquisicaoAnimalEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SpringDataAquisicaoAnimalRepository extends JpaRepository<AquisicaoAnimalEntity, UUID> {

    Optional<AquisicaoAnimalEntity> findByAnimalId(UUID animalId);
}
