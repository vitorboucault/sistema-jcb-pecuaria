package com.br.usecase.dto;

import com.br.core.domain.enums.Categoria;
import com.br.core.domain.enums.Sexo;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record RegistrarCompraAnimalCommand(
        String brincoRgd,
        LocalDate dataNascimento,
        Sexo sexo,
        Categoria categoria,
        UUID loteId,
        LocalDate dataCompra,
        BigDecimal valorCompra
) {}
