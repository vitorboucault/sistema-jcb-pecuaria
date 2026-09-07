package com.br.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record AnimalInputDTO(

        @NotBlank(message = "A origem do animal é obrigatória.")
        String origem,

        @NotBlank(message = "O brinco/RGD é obrigatório.")
        String brincoRgd,

        @NotBlank(message = "A categoria é obrigatória.")
        String categoria,

        @NotBlank(message = "O sexo é obrigatório.")
        String sexo,

        @Positive(message = "O peso deve ser maior que zero.")
        Double peso,

        @NotNull(message = "A data de nascimento é obrigatória.")
        LocalDate dataNascimento,

        LocalDate dataEntrada,

        LocalDate dataCompra,

        @Positive(message = "O valor da compra deve ser maior que zero.")
        BigDecimal valorCompra,

        UUID maeId,

        String loteId

) {}
