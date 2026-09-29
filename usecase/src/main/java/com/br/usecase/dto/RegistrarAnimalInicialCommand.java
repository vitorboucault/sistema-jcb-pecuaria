package com.br.usecase.dto;

import com.br.core.domain.enums.Categoria;
import com.br.core.domain.enums.OrigemAnimal;
import com.br.core.domain.enums.Sexo;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record RegistrarAnimalInicialCommand(
        String brincoRgd,
        LocalDate dataNascimento,
        Sexo sexo,
        Categoria categoria,
        UUID loteId,
        OrigemAnimal origem,
        Double pesoAtual,
        LocalDate dataPesagem,
        LocalDate dataCompraHistorica,
        BigDecimal valorCompraHistorico
) {
    public RegistrarAnimalInicialCommand(
            String brincoRgd,
            LocalDate dataNascimento,
            Sexo sexo,
            Categoria categoria,
            UUID loteId,
            OrigemAnimal origem
    ) {
        this(brincoRgd, dataNascimento, sexo, categoria, loteId, origem, null, null, null, null);
    }

    public RegistrarAnimalInicialCommand(
            String brincoRgd,
            LocalDate dataNascimento,
            Sexo sexo,
            Categoria categoria,
            UUID loteId,
            OrigemAnimal origem,
            Double pesoAtual,
            LocalDate dataPesagem
    ) {
        this(brincoRgd, dataNascimento, sexo, categoria, loteId, origem,
                pesoAtual, dataPesagem, null, null);
    }
}
