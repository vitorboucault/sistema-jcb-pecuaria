package com.sistema.sistemajcb.application.dto.usecase;

import com.sistema.sistemajcb.domain.repository.DespesaRepository;
import com.sistema.sistemajcb.domain.repository.PastoRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

public class CalcularMargemBrutaHectareUseCase {
    private final VendaAnimalRepository vendaRepository;
    private final DespesaRepository despesaRepository;
    private final PastoRepository pastoRepository;

    public CalcularMargemBrutaHectareUseCase(VendaAnimalRepository vendaRepository, DespesaRepository despesaRepository, PastoRepository pastoRepository) {
        this.vendaRepository = vendaRepository;
        this.despesaRepository = despesaRepository;
        this.pastoRepository = pastoRepository;
    }

    public BigDecimal executar(LocalDate inicioSafra, LocalDate fimSafra) {
        // 1. Soma todo o faturamento da fazenda no período
        BigDecimal receitaTotal = vendaRepository.somarReceitasNoPeriodo(inicioSafra, fimSafra);

        // 2. Soma todas as despesas (nutrição, vacina, folha, diesel)
        BigDecimal custoOperacionalTotal = despesaRepository.somarDespesasNoPeriodo(inicioSafra, fimSafra);

        // 3. Busca a área total pastável da fazenda
        Double areaTotalHectares = pastoRepository.somarAreaTotal();

        if (areaTotalHectares == null || areaTotalHectares <= 0) {
            return BigDecimal.ZERO;
        }

        // (Receita - Custos) / Hectares
        BigDecimal lucroBruto = receitaTotal.subtract(custoOperacionalTotal);
        return lucroBruto.divide(BigDecimal.valueOf(areaTotalHectares), 2, RoundingMode.HALF_UP);
    }


}
