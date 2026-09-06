package com.br.usecase.dto;

import com.br.core.domain.enums.Sexo;

import java.time.LocalDate;
import java.util.UUID;

public record RegistrarNascimentoCommand(
        String brincoRgd,
        LocalDate dataNascimento,
        Sexo sexo,
        UUID maeId
) {}
