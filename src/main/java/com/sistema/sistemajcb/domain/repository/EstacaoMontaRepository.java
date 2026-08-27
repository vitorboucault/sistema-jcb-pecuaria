package com.sistema.sistemajcb.domain.repository;

import com.sistema.sistemajcb.domain.model.EstacaoMonta;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EstacaoMontaRepository {
    void salvar(EstacaoMonta estacaoMonta);
    Optional<EstacaoMonta> buscarPorId(UUID id);
    List<EstacaoMonta> buscarEstacoesAbertas();
}
