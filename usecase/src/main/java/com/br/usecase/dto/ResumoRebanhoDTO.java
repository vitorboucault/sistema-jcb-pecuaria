package com.br.usecase.dto;

import com.br.core.domain.enums.Categoria;

import java.util.Map;

public record ResumoRebanhoDTO(
        long total,
        Map<Categoria, Long> porCategoria
) {}
