package com.br.application.dto;

import java.util.UUID;

public record LoteResumoDTO(
        UUID id,
        String nome,
        String fase,
        int quantidadeAnimais,
        Double pesoMedio
) {}
