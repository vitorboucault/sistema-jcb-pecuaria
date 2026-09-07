package com.br.application.dto;

import java.time.LocalDate;
import java.util.UUID;

public record AnimalResumoDTO(
        UUID id,
        String brincoRgd,
        String categoria,
        String sexo,
        UUID loteId,
        String nomeLote,
        Double pesoAtual,
        String status,
        LocalDate dataNascimento,
        LocalDate dataMorte
) {}
