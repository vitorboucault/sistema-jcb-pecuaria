package com.sistema.sistemajcb.domain.repository;

import com.sistema.sistemajcb.domain.model.Pasto;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PastoRepository {
    void salvar(Pasto pasto);
    Optional<Pasto> buscarPorId(UUID id);
    List<Pasto> buscarTodos();
    Double buscarAreaHectares(UUID pastoId);
    Double somarAreaTotal();
}
