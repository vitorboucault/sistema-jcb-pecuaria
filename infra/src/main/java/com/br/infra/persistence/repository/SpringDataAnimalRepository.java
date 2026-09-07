package com.br.infra.persistence.repository;

import com.br.infra.persistence.entity.AnimalEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SpringDataAnimalRepository extends JpaRepository<AnimalEntity, UUID> {

    interface ContagemPorCategoriaProjection {
        String getCategoria();
        long getTotal();
    }

    Optional<AnimalEntity> findByBrincoRgd(String brincoRgd);

    @Query("SELECT a FROM AnimalEntity a WHERE a.status = 'ATIVO' AND " +
            "(a.categoriaAtual IN ('BEZERRO', 'BEZERRA') OR " +
            " a.categoriaAtual = 'GARROTE')")
    List<AnimalEntity> buscarAnimaisElegiveisParaEvolucao();

    List<AnimalEntity> findByLoteAtual(UUID loteAtual);

    long countByStatus(String status);
    Page<AnimalEntity> findByStatus(String status, Pageable pageable);

    @Query("SELECT a FROM AnimalEntity a WHERE a.status = 'ATIVO' OR (a.status = 'MORTO' AND a.dataMorte >= :limiteMorte)")
    Page<AnimalEntity> buscarAnimaisVisiveis(@Param("limiteMorte") LocalDate limiteMorte, Pageable pageable);

    @Query("SELECT a.categoriaAtual AS categoria, COUNT(a.id) AS total FROM AnimalEntity a WHERE a.status = 'ATIVO' GROUP BY a.categoriaAtual")
    List<ContagemPorCategoriaProjection> contarAtivosPorCategoria();

    boolean existsByMaeId(UUID maeId);
}
