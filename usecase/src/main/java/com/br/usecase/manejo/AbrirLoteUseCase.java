package com.br.usecase.manejo;

import com.br.usecase.dto.AbrirLoteCommand;
import com.br.core.domain.model.Lote;
import com.br.core.domain.repository.LoteRepository;
import jakarta.transaction.Transactional;
import jakarta.inject.Named;

import java.util.UUID;

@Named
public class AbrirLoteUseCase {
    private final LoteRepository loteRepository;

    public AbrirLoteUseCase(LoteRepository loteRepository) {
        this.loteRepository = loteRepository;
    }

    @Transactional
    public UUID executar(AbrirLoteCommand command) {
        Lote lote = new Lote(command.nome(), command.fase());
        loteRepository.salvar(lote);
        return lote.getId();
    }
}
