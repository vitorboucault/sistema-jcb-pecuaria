package com.sistema.sistemajcb.infrastructure.persistence.repository;

import com.sistema.sistemajcb.infrastructure.persistence.entity.PesagemEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpringDataPesagemRepository extends JpaRepository<PesagemEntity, UUID> {
    List<PesagemEntity> findByAnimalIdOrderByDataPesagemAsc(UUID animalId);
    Optional<PesagemEntity> findFirstByAnimalIdOrderByDataPesagemDesc(UUID animalId);
}
