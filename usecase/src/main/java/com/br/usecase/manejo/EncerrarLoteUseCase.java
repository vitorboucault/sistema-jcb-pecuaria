package com.br.usecase.manejo;

import com.br.core.domain.model.Lote;
import com.br.core.domain.repository.LoteRepository;
import jakarta.transaction.Transactional;
import jakarta.inject.Named;

import java.util.UUID;

@Named
public class EncerrarLoteUseCase {
    private final LoteRepository loteRepository;

    public EncerrarLoteUseCase(LoteRepository loteRepository) {
        this.loteRepository = loteRepository;
    }

    @Transactional
    public void executar(UUID loteId) {
        Lote lote = loteRepository.buscarPorId(loteId)
                .orElseThrow(() -> new IllegalArgumentException("Lote nao encontrado."));
        lote.encerrarLote();
        loteRepository.salvar(lote);
    }
}
