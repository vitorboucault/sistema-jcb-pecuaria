package com.sistema.sistemajcb.infrastructure.persistence.repository;

import com.sistema.sistemajcb.infrastructure.persistence.entity.DespesaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface SpringDataDespesaRepository extends JpaRepository<DespesaEntity, UUID> {
    List<DespesaEntity> findByTipoAndCentroCustoId(String tipo, UUID centroCustoId);
    List<DespesaEntity> findByTipoAndDataTransacaoBetween(String tipo, LocalDate inicio, LocalDate fim);
}
