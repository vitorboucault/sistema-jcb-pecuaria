package com.br.usecase.dto;

import com.br.core.domain.enums.CategoriaDespesa;
import com.br.core.domain.enums.TipoDeCusto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record RegistrarDespesaCommand(
        String descricao,
        BigDecimal valor,
        LocalDate dataOcorrencia,
        CategoriaDespesa categoria,
        TipoDeCusto tipoDeCusto,
        UUID referenciaId
) { }
