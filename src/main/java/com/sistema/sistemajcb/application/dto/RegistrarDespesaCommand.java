package com.sistema.sistemajcb.application.dto;

import com.sistema.sistemajcb.domain.enums.CategoriaDespesa;
import com.sistema.sistemajcb.domain.enums.TipoDeCusto;

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
