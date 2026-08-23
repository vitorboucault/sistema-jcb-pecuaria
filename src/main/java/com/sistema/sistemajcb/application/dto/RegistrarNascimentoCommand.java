package com.sistema.sistemajcb.application.dto;

import com.sistema.sistemajcb.domain.enums.Sexo;

import java.time.LocalDate;
import java.util.UUID;

public record RegistrarNascimentoCommand(
        String brincoRgd,
        LocalDate dataNascimento,
        Sexo sexo,
        UUID maeId
) {}
