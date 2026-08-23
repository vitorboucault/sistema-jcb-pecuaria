package com.sistema.sistemajcb.infrastructure.persistence.repository;

import com.sistema.sistemajcb.infrastructure.persistence.entity.LoteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SpringDataLoteRepository extends JpaRepository<LoteEntity, UUID>{

    @Query("SELECT l FROM LoteEntity l WHERE l.dataEncerramento IS NULL")
    List<LoteEntity> buscarLotesAtivos();
}
