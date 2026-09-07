package com.br.application.dto;

import com.br.core.domain.enums.CategoriaDespesa;
import com.br.core.domain.enums.TipoDeCusto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record RegistrarDespesaRequest(

        @NotBlank(message = "A descrição é obrigatória.")
        String descricao,

        @NotNull(message = "O valor é obrigatório.")
        @Positive(message = "O valor deve ser maior que zero.")
        BigDecimal valor,

        @NotNull(message = "A data de ocorrência é obrigatória.")
        LocalDate dataOcorrencia,

        @NotNull(message = "A categoria da despesa é obrigatória.")
        CategoriaDespesa categoria,

        @NotNull(message = "O tipo de custo é obrigatório.")
        TipoDeCusto tipoDeCusto,

        UUID referenciaId

) {}
