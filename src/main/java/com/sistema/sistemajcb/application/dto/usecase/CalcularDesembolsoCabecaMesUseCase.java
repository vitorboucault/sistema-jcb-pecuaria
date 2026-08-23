package com.sistema.sistemajcb.application.dto.usecase;

import com.sistema.sistemajcb.domain.model.Despesa;
import com.sistema.sistemajcb.domain.repository.DespesaRepository;
import com.sistema.sistemajcb.domain.repository.AnimalRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@Service
public class CalcularDesembolsoCabecaMesUseCase {
    private final DespesaRepository despesaRepository;
    private final AnimalRepository animalRepository;

    public CalcularDesembolsoCabecaMesUseCase(DespesaRepository despesaRepository, AnimalRepository animalRepository) {
        this.despesaRepository = despesaRepository;
        this.animalRepository = animalRepository;
    }

    public BigDecimal executar(int ano, int mes) {

        YearMonth anoMes = YearMonth.of(ano, mes);
        LocalDate inicioDoMes = anoMes.atDay(1);
        LocalDate fimDoMes = anoMes.atEndOfMonth();

        List<Despesa> despesasDoMes = despesaRepository.buscarPorPeriodo(inicioDoMes, fimDoMes);

        BigDecimal despesaTotal = despesasDoMes.stream()
                .map(Despesa::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (despesaTotal.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }

        long totalCabecas = animalRepository.contarAnimaisAtivos();
//        long cabecasNoLote = animalRepository.contarAnimaisNoLote(loteId);
//        double percentualDoLote = (double) cabecasNoLote / totalCabecasFazenda;
//        BigDecimal custoGeralRateadoParaOLote = totalDespesasGeraisDaFazenda
//                .multiply(BigDecimal.valueOf(percentualDoLote));

        if (totalCabecas == 0) {
            throw new IllegalStateException("Despesas registradas, mas não há animais ativos na fazenda para ratear o custo.");
        }

        return despesaTotal.divide(BigDecimal.valueOf(totalCabecas), 2, RoundingMode.HALF_UP);
    }
}
