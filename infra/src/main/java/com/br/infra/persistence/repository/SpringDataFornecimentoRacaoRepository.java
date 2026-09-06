package com.br.infra.persistence.repository;

import com.br.infra.persistence.entity.FornecimentoRacaoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.UUID;

public interface SpringDataFornecimentoRacaoRepository extends JpaRepository<FornecimentoRacaoEntity, UUID> {

    @Query("SELECT COALESCE(SUM(f.quantidadeKg * (f.teorMateriaSeca / 100.0)), 0.0) " +
            "FROM FornecimentoRacaoEntity f " +
            "WHERE f.loteId = :loteId AND f.dataFornecimento BETWEEN :inicio AND :fim")
    Double somarConsumoMateriaSecaPorLoteNoPeriodo(
            @Param("loteId") UUID loteId,
            @Param("inicio") LocalDate inicio,
            @Param("fim") LocalDate fim
    );
}
