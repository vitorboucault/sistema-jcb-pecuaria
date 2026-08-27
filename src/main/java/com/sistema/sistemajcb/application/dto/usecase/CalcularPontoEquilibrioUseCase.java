package com.sistema.sistemajcb.application.dto.usecase;

import com.sistema.sistemajcb.domain.repository.DespesaRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

public class CalcularPontoEquilibrioUseCase {
    private final DespesaRepository despesaRepository;

    public CalcularPontoEquilibrioUseCase(DespesaRepository despesaRepository) {
        this.despesaRepository = despesaRepository;
    }

    public BigDecimal executar(LocalDate inicioSafra, LocalDate fimSafra, BigDecimal precoArrobaMercado) {
        if (precoArrobaMercado.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("O preço da arroba deve ser maior que zero.");
        }

        BigDecimal custoTotalOperacional = despesaRepository.somarDespesasNoPeriodo(inicioSafra, fimSafra);

        return custoTotalOperacional.divide(precoArrobaMercado, 2, RoundingMode.HALF_UP);
    }

}
