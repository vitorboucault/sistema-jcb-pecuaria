package com.br.core.domain.repository;

import com.br.core.domain.model.DiagnosticoGestacao;

import java.util.List;
import java.util.UUID;

public interface DiagnosticoGestacaoRepository {
    void salvar(DiagnosticoGestacao diagnostico);
    List<DiagnosticoGestacao> buscarPorEstacaoMonta(UUID estacaoMontaId);
    boolean existePorAnimalId(UUID animalId);
}
