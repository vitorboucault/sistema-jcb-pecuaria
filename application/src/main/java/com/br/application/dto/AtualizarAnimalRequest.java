package com.br.application.dto;

import com.br.core.domain.enums.Categoria;
import com.br.core.domain.enums.Sexo;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record AtualizarAnimalRequest(
        @NotBlank(message = "O brinco/RGD é obrigatório.")
        String brincoRgd,

        @NotNull(message = "A data de nascimento é obrigatória.")
        LocalDate dataNascimento,

        @NotNull(message = "O sexo é obrigatório.")
        Sexo sexo,

        @NotNull(message = "A categoria é obrigatória.")
        Categoria categoria
) {}
