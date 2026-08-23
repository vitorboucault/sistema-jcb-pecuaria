package com.sistema.sistemajcb.infrastructure.persistence.repository;

import com.sistema.sistemajcb.infrastructure.persistence.entity.AnimalEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SpringDataAnimalRepository extends JpaRepository<AnimalEntity, UUID> {

    Optional<AnimalEntity> findByBrincoRgd(String brincoRgd);

    @Query("SELECT a FROM AnimalEntity a WHERE a.status = 'ATIVO' AND " +
            "(a.categoriaAtual IN ('BEZERRO', 'BEZERRA') OR " +
            " a.categoriaAtual = 'GARROTE')")
    List<AnimalEntity> buscarAnimaisElegiveisParaEvolucao();

    List<AnimalEntity> findByLoteAtual(UUID loteAtual);

    long countByStatus(String status);
}
