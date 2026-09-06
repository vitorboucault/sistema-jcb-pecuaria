package com.br.usecase.financeiro;

import com.br.core.domain.model.Animal;
import com.br.core.domain.model.Despesa;
import com.br.core.domain.model.Pesagem;
import com.br.core.domain.repository.AnimalRepository;
import com.br.core.domain.repository.DespesaRepository;
import com.br.core.domain.repository.PesagemRepository;
import jakarta.inject.Named;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Named
public class CalcularCustoArrobaUseCase {

    private final DespesaRepository despesaRepository;
    private final PesagemRepository pesagemRepository;
    private final AnimalRepository animalRepository;

    public CalcularCustoArrobaUseCase(DespesaRepository despesaRepository, PesagemRepository pesagemRepository, AnimalRepository animalRepository) {
        this.despesaRepository = despesaRepository;
        this.pesagemRepository = pesagemRepository;
        this.animalRepository = animalRepository;
    }

    public BigDecimal executar(LocalDate inicioSafra, LocalDate fimSafra) {
        BigDecimal custoTotalOperacional = despesaRepository.somarDespesasNoPeriodo(inicioSafra, fimSafra);

        if (custoTotalOperacional.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }

        Double ganhoPesoTotalKg = pesagemRepository.calcularGanhoPesoTotalNoPeriodo(inicioSafra, fimSafra);

        if (ganhoPesoTotalKg == null || ganhoPesoTotalKg <= 0) {
            return BigDecimal.ZERO;
        }

        double arrobasProduzidas = ganhoPesoTotalKg / 30.0;
        return custoTotalOperacional.divide(BigDecimal.valueOf(arrobasProduzidas), 2, RoundingMode.HALF_UP);
    }

    public BigDecimal executar(UUID loteId) {
        List<Despesa> despesasDoLote = despesaRepository.buscarPorLote(loteId);
        BigDecimal custoTotal = despesasDoLote.stream()
                .map(Despesa::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (custoTotal.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }

        List<Animal> animaisDoLote = animalRepository.buscarPorLote(loteId);
        double ganhoDePesoTotalKg = 0.0;

        for (Animal animal : animaisDoLote) {
            List<Pesagem> historico = pesagemRepository.buscarHistoricoPorAnimal(animal.getId());
            if (historico.size() >= 2) {
                Pesagem pesoInicial = historico.get(0);
                Pesagem pesoFinal = historico.get(historico.size() - 1);
                double ganhoIndividual = pesoFinal.getPeso() - pesoInicial.getPeso();

                if (ganhoIndividual > 0) {
                    ganhoDePesoTotalKg += ganhoIndividual;
                }
            }
        }

        if (ganhoDePesoTotalKg <= 0) {
            return BigDecimal.ZERO;
        }

        double arrobasProduzidas = ganhoDePesoTotalKg / 30.0;
        return custoTotal.divide(BigDecimal.valueOf(arrobasProduzidas), 2, RoundingMode.HALF_UP);
    }
}