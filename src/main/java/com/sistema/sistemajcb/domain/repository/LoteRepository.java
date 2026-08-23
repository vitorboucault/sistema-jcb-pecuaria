package com.sistema.sistemajcb.domain.repository;

import com.sistema.sistemajcb.domain.model.Lote;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public interface LoteRepository {
    void salvar(Lote lote);
    Optional<Lote> buscarPorId(UUID id);
    List<Lote> buscarAtivos();
}
