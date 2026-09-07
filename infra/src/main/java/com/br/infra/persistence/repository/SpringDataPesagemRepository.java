package com.br.infra.persistence.repository;

import com.br.infra.persistence.entity.PesagemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SpringDataPesagemRepository extends JpaRepository<PesagemEntity, UUID> {
    List<PesagemEntity> findByAnimalIdOrderByDataPesagemAsc(UUID animalId);
    Optional<PesagemEntity> findFirstByAnimalIdOrderByDataPesagemDesc(UUID animalId);
    boolean existsByAnimalId(UUID animalId);

    @Query("""
            SELECT p FROM PesagemEntity p
            WHERE p.animalId IN :animalIds
            AND p.dataPesagem = (
                SELECT MAX(p2.dataPesagem)
                FROM PesagemEntity p2
                WHERE p2.animalId = p.animalId
            )
            """)
    List<PesagemEntity> buscarUltimasPesagensPorAnimalIds(@Param("animalIds") List<UUID> animalIds);

    @Query("SELECT COALESCE(SUM(p.peso), 0.0) FROM PesagemEntity p WHERE p.dataPesagem BETWEEN :inicio AND :fim")
    Double calcularGanhoPesoTotalNoPeriodo(@Param("inicio") LocalDate inicio, @Param("fim") LocalDate fim);
}
