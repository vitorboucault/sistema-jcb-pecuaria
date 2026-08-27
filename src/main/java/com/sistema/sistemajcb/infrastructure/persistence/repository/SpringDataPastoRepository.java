package com.sistema.sistemajcb.infrastructure.persistence.repository;

import com.sistema.sistemajcb.infrastructure.persistence.entity.PastoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SpringDataPastoRepository extends JpaRepository<PastoEntity, UUID> {
    @Query("SELECT COALESCE(SUM(p.areaHectares), 0.0) FROM PastoEntity p")
    Double somarAreaTotalHectares();
}
