package com.sistema.sistemajcb.domain.repository;

import com.sistema.sistemajcb.domain.model.Desmame;
import com.sistema.sistemajcb.domain.model.Despesa;

import java.util.List;
import java.util.UUID;

public interface DesmameRepository {
    void salvar(Desmame desmame);
    long contarDesmamesPorEstacao(UUID estacaoMontaId);
}
