package com.br.application.dto;

import java.time.LocalDate;
import java.util.UUID;

public record AnimalInputDTO(
        String origem,
        String brincoRgd,
        String categoria,
        String sexo,
        Double peso,
        LocalDate dataNascimento,
        LocalDate dataEntrada,
        UUID maeId,
        String loteId
) {}
