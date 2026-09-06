package com.br.infra.persistence.repository;

import com.br.infra.persistence.entity.DesmameEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import java.util.UUID;

@Repository
public interface SpringDataDesmameRepository extends JpaRepository<DesmameEntity, UUID> {
    long countByEstacaoMontaId(UUID estacaoMontaId);
}
