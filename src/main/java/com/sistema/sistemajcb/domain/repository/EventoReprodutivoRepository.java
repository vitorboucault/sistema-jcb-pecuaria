package com.sistema.sistemajcb.domain.repository;

import com.sistema.sistemajcb.domain.model.EventoReprodutivo;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface EventoReprodutivoRepository {
    void salvar(EventoReprodutivo evento);
    long contarFemeasUnicasNaEstacao(UUID estacaoMontaId);
}
