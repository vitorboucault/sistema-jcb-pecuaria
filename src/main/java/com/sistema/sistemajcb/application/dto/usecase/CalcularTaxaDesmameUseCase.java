package com.sistema.sistemajcb.application.dto.usecase;

import com.sistema.sistemajcb.domain.repository.DesmameRepository;
import com.sistema.sistemajcb.domain.repository.EventoReprodutivoRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

public class CalcularTaxaDesmameUseCase {
    private final EventoReprodutivoRepository eventoRepository;
    private final DesmameRepository desmameRepository;

    public CalcularTaxaDesmameUseCase(EventoReprodutivoRepository eventoRepository, DesmameRepository desmameRepository) {
        this.eventoRepository = eventoRepository;
        this.desmameRepository = desmameRepository;
    }

    public BigDecimal executar(UUID estacaoMontaId) {
        // Quantas vacas entraram no desafio reprodutivo?
        long totalFemeasExpostas = eventoRepository.contarFemeasUnicasNaEstacao(estacaoMontaId);

        if (totalFemeasExpostas == 0) {
            return BigDecimal.ZERO;
        }

        // Quantos bezerros dessa safra/estação chegaram vivos ao desmame?
        long bezerrosDesmamados = desmameRepository.contarDesmamesPorEstacao(estacaoMontaId);

        // (Desmamados / Matrizes Expostas) * 100
        BigDecimal taxa = BigDecimal.valueOf((double) bezerrosDesmamados / totalFemeasExpostas * 100);
        return taxa.setScale(2, RoundingMode.HALF_UP);
    }
}
