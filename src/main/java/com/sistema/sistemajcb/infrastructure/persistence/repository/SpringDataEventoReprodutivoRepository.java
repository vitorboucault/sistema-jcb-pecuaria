package com.sistema.sistemajcb.infrastructure.persistence.repository;

import com.sistema.sistemajcb.infrastructure.persistence.entity.EventoReprodutivoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SpringDataEventoReprodutivoRepository extends JpaRepository<EventoReprodutivoEntity, UUID> {
    @Query("SELECT COUNT(DISTINCT e.animalId) FROM EventoReprodutivoEntity e WHERE e.estacaoMontaId = :estacaoMontaId")
    long countDistinctAnimalIdByEstacaoMontaId(@Param("estacaoMontaId") UUID estacaoMontaId);
}
