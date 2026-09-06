package com.br.usecase.zootecnico;

import com.br.core.domain.enums.ResultadoDiagnostico;
import com.br.core.domain.model.DiagnosticoGestacao;
import com.br.core.domain.repository.DiagnosticoGestacaoRepository;
import com.br.core.domain.repository.EventoReprodutivoRepository;
import jakarta.inject.Named;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.UUID;

@Named
public class CalcularTaxaPrenhezUseCase {

    private final EventoReprodutivoRepository eventoRepository;
    private final DiagnosticoGestacaoRepository diagnosticoRepository;

    public CalcularTaxaPrenhezUseCase(EventoReprodutivoRepository eventoRepository, DiagnosticoGestacaoRepository diagnosticoRepository) {
        this.eventoRepository = eventoRepository;
        this.diagnosticoRepository = diagnosticoRepository;
    }

    public BigDecimal executar(UUID estacaoMontaId) {
        // Busca o número total de matrizes únicas que entraram em reproduçao na estaçao
        long totalFemeasExpostas = eventoRepository.contarFemeasUnicasNaEstacao(estacaoMontaId);

        if (totalFemeasExpostas == 0) {
            return BigDecimal.ZERO;
        }

        // Busca os diagnósticos da estaçao e filtra apenas os positivos (PRENHE)
        List<DiagnosticoGestacao> diagnosticos = diagnosticoRepository.buscarPorEstacaoMonta(estacaoMontaId);
        long totalPrenhes = diagnosticos.stream()
                .filter(d -> d.getResultado() == ResultadoDiagnostico.PRENHE)
                .count();

        // (Prenhas / Expostas) * 100
        BigDecimal taxa = BigDecimal.valueOf((double) totalPrenhes / totalFemeasExpostas * 100);
        return taxa.setScale(2, RoundingMode.HALF_UP);
    }
}
