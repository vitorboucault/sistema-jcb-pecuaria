package com.br.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record RegistrarPastoRequest(

        @NotBlank(message = "O nome do pasto é obrigatório.")
        String nome,

        @Positive(message = "A área do pasto deve ser maior que zero.")
        double areaHectares,

        @Positive(message = "A capacidade de suporte deve ser maior que zero.")
        double capacidadeSuporteUa

) {}