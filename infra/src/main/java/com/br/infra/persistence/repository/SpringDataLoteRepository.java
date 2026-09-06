package com.br.infra.persistence.repository;

import com.br.infra.persistence.entity.LoteEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SpringDataLoteRepository extends JpaRepository<LoteEntity, UUID>{

    @Query("SELECT l FROM LoteEntity l WHERE l.dataEncerramento IS NULL")
    List<LoteEntity> buscarLotesAtivos();
    Page<LoteEntity> findByDataEncerramentoIsNull(Pageable pageable);
}
