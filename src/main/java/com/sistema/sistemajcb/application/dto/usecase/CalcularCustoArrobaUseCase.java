package com.sistema.sistemajcb.application.dto.usecase;

import com.sistema.sistemajcb.domain.Animal;
import com.sistema.sistemajcb.domain.model.Despesa;
import com.sistema.sistemajcb.domain.model.Pesagem;
import com.sistema.sistemajcb.domain.repository.DespesaRepository;
import com.sistema.sistemajcb.domain.repository.PesagemRepository;
import com.sistema.sistemajcb.domain.repository.AnimalRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.UUID;

@Service
public class CalcularCustoArrobaUseCase {
    private final DespesaRepository despesaRepository;
    private final AnimalRepository animalRepository;
    private final PesagemRepository pesagemRepository;

    private static final double KG_VIVO_POR_ARROBA = 30.0;

    public CalcularCustoArrobaUseCase(DespesaRepository despesaRepository, AnimalRepository animalRepository, PesagemRepository pesagemRepository) {
        this.despesaRepository = despesaRepository;
        this.animalRepository = animalRepository;
        this.pesagemRepository = pesagemRepository;
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

                double ganhoIndividual = pesoFinal.getPesoKg() - pesoInicial.getPesoKg();
                ganhoDePesoTotalKg += ganhoIndividual;
            }
        }

        if (ganhoDePesoTotalKg <= 0) {
            throw new IllegalStateException("ALERTA VERMELHO: O lote perdeu peso ou ficou estagnado no período. " +
                    "Não houve produção de arrobas. Todo o valor investido (R$ " + custoTotal + ") foi despesa de manutenção ou prejuízo.");
        }

        double arrobasProduzidas = ganhoDePesoTotalKg / KG_VIVO_POR_ARROBA;

        return custoTotal.divide(BigDecimal.valueOf(arrobasProduzidas), 2, RoundingMode.HALF_UP);
    }
}
