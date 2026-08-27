package com.sistema.sistemajcb.application.dto.usecase;

import com.sistema.sistemajcb.domain.enums.ResultadoDiagnostico;
import com.sistema.sistemajcb.domain.model.DiagnosticoGestacao;
import com.sistema.sistemajcb.domain.repository.DiagnosticoGestacaoRepository;
import com.sistema.sistemajcb.domain.repository.EventoReprodutivoRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.UUID;

@Service
public class CalcularTaxaPrenhezUseCase {

    private final EventoReprodutivoRepository eventoRepository;
    private final DiagnosticoGestacaoRepository diagnosticoRepository;

    public CalcularTaxaPrenhezUseCase(EventoReprodutivoRepository eventoRepository, DiagnosticoGestacaoRepository diagnosticoRepository) {
        this.eventoRepository = eventoRepository;
        this.diagnosticoRepository = diagnosticoRepository;
    }

    public BigDecimal executar(UUID estacaoMontaId) {
        // Busca o número total de matrizes únicas que entraram em reprodução na estação
        long totalFemeasExpostas = eventoRepository.contarFemeasUnicasNaEstacao(estacaoMontaId);

        if (totalFemeasExpostas == 0) {
            return BigDecimal.ZERO;
        }

        // Busca os diagnósticos da estação e filtra apenas os positivos (PRENHE)
        List<DiagnosticoGestacao> diagnosticos = diagnosticoRepository.buscarPorEstacaoMonta(estacaoMontaId);
        long totalPrenhes = diagnosticos.stream()
                .filter(d -> d.getResultado() == ResultadoDiagnostico.PRENHE)
                .count();

        // (Prenhas / Expostas) * 100
        BigDecimal taxa = BigDecimal.valueOf((double) totalPrenhes / totalFemeasExpostas * 100);
        return taxa.setScale(2, RoundingMode.HALF_UP);
    }
}
