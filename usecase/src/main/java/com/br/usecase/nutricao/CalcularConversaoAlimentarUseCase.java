package com.br.usecase.nutricao;

import com.br.core.domain.model.Animal;
import com.br.core.domain.model.Pesagem;
import com.br.core.domain.repository.AnimalRepository;
import com.br.core.domain.repository.PesagemRepository;
import com.br.usecase.port.FornecimentoRacaoRepositoryPort;
import jakarta.inject.Named;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Named
public class CalcularConversaoAlimentarUseCase {

    private final FornecimentoRacaoRepositoryPort fornecimentoRepository;
    private final AnimalRepository animalRepository;
    private final PesagemRepository pesagemRepository;

    public CalcularConversaoAlimentarUseCase(
            FornecimentoRacaoRepositoryPort fornecimentoRepository,
            AnimalRepository animalRepository,
            PesagemRepository pesagemRepository) {
        this.fornecimentoRepository = fornecimentoRepository;
        this.animalRepository = animalRepository;
        this.pesagemRepository = pesagemRepository;
    }

    public BigDecimal executar(UUID loteId, LocalDate inicio, LocalDate fim) {
        Double consumoMsTotal = fornecimentoRepository.somarConsumoMateriaSecaPorLoteNoPeriodo(loteId, inicio, fim);
        if (consumoMsTotal == null || consumoMsTotal <= 0.0) {
            return BigDecimal.ZERO;
        }

        List<Animal> animais = animalRepository.buscarPorLote(loteId);
        double ganhoPesoTotalKg = 0.0;

        for (Animal animal : animais) {
            List<Pesagem> historico = pesagemRepository.buscarHistoricoPorAnimal(animal.getId());
            if (historico.size() >= 2) {
                Pesagem pesoInicial = historico.getFirst();
                Pesagem pesoFinal = historico.getLast();
                double ganho = pesoFinal.getPeso() - pesoInicial.getPeso();
                if (ganho > 0) {
                    ganhoPesoTotalKg += ganho;
                }
            }
        }

        if (ganhoPesoTotalKg <= 0.0) {
            return BigDecimal.ZERO;
        }

        double ca = consumoMsTotal / ganhoPesoTotalKg;
        return BigDecimal.valueOf(ca).setScale(2, RoundingMode.HALF_UP);
    }
}