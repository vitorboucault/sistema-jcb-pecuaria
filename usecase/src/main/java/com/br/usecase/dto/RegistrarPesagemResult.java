package com.br.usecase.dto;

import java.util.UUID;

public record RegistrarPesagemResult(
        UUID pesagemId,
        double pesoKg,
        double gmd
) { }
