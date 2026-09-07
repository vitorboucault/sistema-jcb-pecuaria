package com.br.usecase.manejo;

import com.br.core.domain.model.Pasto;
import com.br.core.domain.repository.PastoRepository;
import jakarta.inject.Named;
import jakarta.transaction.Transactional;

import java.util.UUID;

@Named
public class IniciarManutencaoPastoUseCase {
    private final PastoRepository pastoRepository;

    public IniciarManutencaoPastoUseCase(PastoRepository pastoRepository) {
        this.pastoRepository = pastoRepository;
    }

    @Transactional
    public void executar(UUID pastoId) {
        Pasto pasto = pastoRepository.buscarPorId(pastoId)
                .orElseThrow(() -> new IllegalArgumentException("Pasto nao encontrado."));
        pasto.iniciarManutencao();
        pastoRepository.salvar(pasto);
    }
}
