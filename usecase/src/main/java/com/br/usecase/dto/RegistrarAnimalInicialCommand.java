package com.br.usecase.dto;

import com.br.core.domain.enums.Categoria;
import com.br.core.domain.enums.OrigemAnimal;
import com.br.core.domain.enums.Sexo;

import java.time.LocalDate;
import java.util.UUID;

public record RegistrarAnimalInicialCommand(
        String brincoRgd,
        LocalDate dataNascimento,
        Sexo sexo,
        Categoria categoria,
        UUID loteId,
        OrigemAnimal origem
) {
}
