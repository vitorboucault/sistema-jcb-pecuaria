package com.br.core.domain.repository;

import com.br.core.domain.model.EventoReprodutivo;
import java.util.UUID;

public interface EventoReprodutivoRepository {
    void salvar(EventoReprodutivo evento);
    long contarFemeasUnicasNaEstacao(UUID estacaoMontaId);
}
