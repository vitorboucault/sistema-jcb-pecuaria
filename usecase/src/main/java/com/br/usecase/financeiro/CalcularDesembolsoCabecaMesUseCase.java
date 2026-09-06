package com.br.usecase.financeiro;

import com.br.core.domain.repository.DespesaRepository;
import com.br.core.domain.repository.AnimalRepository;
import jakarta.inject.Named;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Named
public class CalcularDesembolsoCabecaMesUseCase {

    private final DespesaRepository despesaRepository;
    private final AnimalRepository animalRepository;

    public CalcularDesembolsoCabecaMesUseCase(DespesaRepository despesaRepository, AnimalRepository animalRepository) {
        this.despesaRepository = despesaRepository;
        this.animalRepository = animalRepository;
    }

    public BigDecimal executar(LocalDate inicioSafra, LocalDate fimSafra) {
        BigDecimal custoTotalOperacional = despesaRepository.somarDespesasNoPeriodo(inicioSafra, fimSafra);

        if (custoTotalOperacional.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }

        long totalAnimais = animalRepository.contarAnimaisAtivos();

        if (totalAnimais == 0) {
            return BigDecimal.ZERO;
        }

        long dias = ChronoUnit.DAYS.between(inicioSafra, fimSafra);
        double meses = dias / 30.0;

        if (meses <= 0) {
            meses = 1.0;
        }

        // Desembolso = (Custo / Animais) / Meses
        double desembolso = (custoTotalOperacional.doubleValue() / totalAnimais) / meses;

        return BigDecimal.valueOf(desembolso).setScale(2, RoundingMode.HALF_UP);
    }
}