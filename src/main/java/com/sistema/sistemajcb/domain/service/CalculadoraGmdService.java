package com.sistema.sistemajcb.domain.service;

import com.sistema.sistemajcb.domain.model.Pesagem;

import java.time.temporal.ChronoUnit;

public class CalculadoraGmdService {
    public double calcularGmd(Pesagem pesagemAnterior, Pesagem pesagemAtual) {
        if (pesagemAnterior == null || pesagemAtual == null) {
            return 0.0;
        }

        if (pesagemAtual.getDataPesagem().isBefore(pesagemAnterior.getDataPesagem())) {
            throw new IllegalArgumentException("A pesagem atual não pode ser anterior à pesagem base.");
        }

        long dias = ChronoUnit.DAYS.between(pesagemAnterior.getDataPesagem(), pesagemAtual.getDataPesagem());

        if (dias == 0) {
            return 0.0; // Pesagens no mesmo dia não geram GMD
        }

        double ganhoPeso = pesagemAtual.getPesoKg() - pesagemAnterior.getPesoKg();

        return ganhoPeso / dias;
    }
}
