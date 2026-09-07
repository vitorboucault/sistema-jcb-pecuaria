package com.br.usecase.dto;

import java.time.LocalDate;
import java.util.UUID;

public record RegistrarMorteAnimalCommand(
        UUID animalId,
        LocalDate dataMorte
) {}
