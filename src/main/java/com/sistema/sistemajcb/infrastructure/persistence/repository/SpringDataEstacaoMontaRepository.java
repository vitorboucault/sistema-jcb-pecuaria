package com.sistema.sistemajcb.infrastructure.persistence.repository;

import com.sistema.sistemajcb.infrastructure.persistence.entity.EstacaoMontaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SpringDataEstacaoMontaRepository extends JpaRepository<EstacaoMontaEntity, UUID> {
    List<EstacaoMontaEntity> findByStatus(String status);
}
