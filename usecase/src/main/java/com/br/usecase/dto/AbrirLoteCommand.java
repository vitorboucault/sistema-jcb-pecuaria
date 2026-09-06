package com.br.usecase.dto;

import com.br.core.domain.enums.FaseLote;

public record AbrirLoteCommand(
        String nome,
        FaseLote fase
) { }
