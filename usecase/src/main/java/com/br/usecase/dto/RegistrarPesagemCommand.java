package com.br.usecase.dto;

import com.br.core.domain.enums.OrigemPesagem;

import java.time.LocalDate;
import java.util.UUID;

public record RegistrarPesagemCommand(
        UUID animalId,
        LocalDate dataPesagem,
        double pesoKg,
        boolean jejum,
        OrigemPesagem origem
) {
    public RegistrarPesagemCommand(UUID animalId, LocalDate dataPesagem, double pesoKg, boolean jejum) {
        this(animalId, dataPesagem, pesoKg, jejum, OrigemPesagem.OPERACIONAL);
    }
}
