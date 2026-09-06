package com.br.usecase.zootecnico;

import com.br.core.domain.repository.DesmameRepository;
import com.br.core.domain.repository.EventoReprodutivoRepository;
import jakarta.inject.Named;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

@Named
public class CalcularTaxaDesmameUseCase {
    private final EventoReprodutivoRepository eventoRepository;
    private final DesmameRepository desmameRepository;

    public CalcularTaxaDesmameUseCase(EventoReprodutivoRepository eventoRepository, DesmameRepository desmameRepository) {
        this.eventoRepository = eventoRepository;
        this.desmameRepository = desmameRepository;
    }

    public BigDecimal executar(UUID estacaoMontaId) {

        long totalFemeasExpostas = eventoRepository.contarFemeasUnicasNaEstacao(estacaoMontaId);

        if (totalFemeasExpostas == 0) {
            return BigDecimal.ZERO;
        }

        long bezerrosDesmamados = desmameRepository.contarDesmamesPorEstacao(estacaoMontaId);

        BigDecimal taxa = BigDecimal.valueOf((double) bezerrosDesmamados / totalFemeasExpostas * 100);
        return taxa.setScale(2, RoundingMode.HALF_UP);
    }
}
