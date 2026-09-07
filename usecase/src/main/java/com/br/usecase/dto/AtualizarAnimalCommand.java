package com.br.usecase.dto;

import com.br.core.domain.enums.Categoria;
import com.br.core.domain.enums.Sexo;

import java.time.LocalDate;
import java.util.UUID;

public record AtualizarAnimalCommand(
        UUID animalId,
        String brincoRgd,
        LocalDate dataNascimento,
        Sexo sexo,
        Categoria categoria
) {}
