package com.br.usecase.financeiro;

import com.br.core.domain.model.Pesagem;
import com.br.core.domain.repository.PesagemRepository;
import com.br.core.domain.service.CalculadoraGmdService;
import jakarta.inject.Named;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Named
public class CalcularGmdGlobalUseCase {

    private final PesagemRepository pesagemRepository;
    private final CalculadoraGmdService calculadoraGmd;

    public CalcularGmdGlobalUseCase(PesagemRepository pesagemRepository) {
        this.pesagemRepository = pesagemRepository;
        this.calculadoraGmd = new CalculadoraGmdService();
    }

    public BigDecimal executar(LocalDate inicio, LocalDate fim, List<UUID> animaisAtivos) {
        double somaGmd = 0.0;
        int animaisComGanhoValido = 0;

        for (UUID animalId : animaisAtivos) {
            List<Pesagem> historico = pesagemRepository.buscarHistoricoPorAnimal(animalId);
            if (historico.size() >= 2) {
                double gmdIndividual = calculadoraGmd.calcularGmd(historico.get(0), historico.get(historico.size() - 1));
                if (gmdIndividual > 0) {
                    somaGmd += gmdIndividual;
                    animaisComGanhoValido++;
                }
            }
        }

        if (animaisComGanhoValido == 0) return BigDecimal.ZERO;

        return BigDecimal.valueOf(somaGmd / animaisComGanhoValido).setScale(3, RoundingMode.HALF_UP);
    }
}