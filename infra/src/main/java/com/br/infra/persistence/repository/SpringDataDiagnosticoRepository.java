package com.br.infra.persistence.repository;

import com.br.infra.persistence.entity.DiagnosticoGestacaoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import java.util.List;
import java.util.UUID;

@Repository
public interface SpringDataDiagnosticoRepository extends JpaRepository<DiagnosticoGestacaoEntity, UUID> {
    List<DiagnosticoGestacaoEntity> findByEstacaoMontaId(UUID estacaoMontaId);
}
