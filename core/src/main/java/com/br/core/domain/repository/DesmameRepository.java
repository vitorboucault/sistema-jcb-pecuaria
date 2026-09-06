package com.br.core.domain.repository;

import com.br.core.domain.model.Desmame;
import com.br.core.domain.model.Despesa;

import java.util.List;
import java.util.UUID;

public interface DesmameRepository {
    void salvar(Desmame desmame);
    long contarDesmamesPorEstacao(UUID estacaoMontaId);
}
