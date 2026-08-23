package com.sistema.sistemajcb.infrastructure.persistence.scheduler;

import com.sistema.sistemajcb.application.dto.usecase.EvoluirCategoriaUseCase;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class EvolucaoCategoriaJob {
    private final EvoluirCategoriaUseCase evoluirCategoriaUseCase;

    public EvolucaoCategoriaJob(EvoluirCategoriaUseCase evoluirCategoriaUseCase) {
        this.evoluirCategoriaUseCase = evoluirCategoriaUseCase;
    }

    @Scheduled(cron = "0 0 2 * * *")
    public void processarEvolucaoDiaria() {
        System.out.println("Iniciando rotina de evolução de categoria do rebanho...");
        evoluirCategoriaUseCase.executar();
    }

}
