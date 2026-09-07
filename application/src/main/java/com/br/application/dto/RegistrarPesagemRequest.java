package com.br.application.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;
import java.util.UUID;

public record RegistrarPesagemRequest(

        @NotNull(message = "O animal é obrigatório.")
        UUID animalId,

        @NotNull(message = "A data da pesagem é obrigatória.")
        LocalDate dataPesagem,

        @Positive(message = "O peso deve ser maior que zero.")
        double pesoKg,

        boolean jejum

) {
}