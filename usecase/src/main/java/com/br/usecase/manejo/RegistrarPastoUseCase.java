package com.br.usecase.manejo;

import com.br.usecase.dto.RegistrarPastoCommand;
import com.br.core.domain.model.Pasto;
import com.br.core.domain.repository.PastoRepository;
import jakarta.transaction.Transactional;
import jakarta.inject.Named;

import java.util.UUID;

@Named
public class RegistrarPastoUseCase {
    private final PastoRepository pastoRepository;

    public RegistrarPastoUseCase(PastoRepository pastoRepository) {
        this.pastoRepository = pastoRepository;
    }

    @Transactional
    public UUID executar(RegistrarPastoCommand command) {
        Pasto pasto = new Pasto(command.nome(), command.areaHectares(), command.capacidadeSuporteUa());
        pastoRepository.salvar(pasto);
        return pasto.getId();
    }
}
