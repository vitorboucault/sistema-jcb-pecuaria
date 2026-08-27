package com.sistema.sistemajcb.domain.repository;

import com.sistema.sistemajcb.domain.model.DiagnosticoGestacao;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DiagnosticoGestacaoRepository {
    void salvar(DiagnosticoGestacao diagnostico);
    List<DiagnosticoGestacao> buscarPorEstacaoMonta(UUID estacaoMontaId);
}
