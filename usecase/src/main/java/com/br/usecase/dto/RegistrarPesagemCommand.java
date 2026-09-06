package com.br.usecase.dto;

import java.time.LocalDate;
import java.util.UUID;

public record RegistrarPesagemCommand(
        UUID animalId,
        LocalDate dataPesagem,
        double pesoKg,
        boolean jejum
) { }
