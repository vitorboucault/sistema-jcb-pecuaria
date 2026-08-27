package com.sistema.sistemajcb.infrastructure.persistence.repository;

import com.sistema.sistemajcb.infrastructure.persistence.entity.MovimentacaoLoteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SpringDataMovimentacaoLoteRepository extends JpaRepository<MovimentacaoLoteEntity, UUID> {
    List<MovimentacaoLoteEntity> findByPastoIdAndDataSaidaIsNull(UUID pastoId);
}
