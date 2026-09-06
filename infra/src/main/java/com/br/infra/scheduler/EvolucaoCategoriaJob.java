package com.br.infra.scheduler;

import com.br.usecase.manejo.EvoluirCategoriaUseCase;
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
        System.out.println("Iniciando rotina de evoluçao de categoria do rebanho...");
        evoluirCategoriaUseCase.executar();
    }

}
