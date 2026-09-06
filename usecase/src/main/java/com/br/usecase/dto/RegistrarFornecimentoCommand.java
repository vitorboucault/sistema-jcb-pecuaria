package com.br.usecase.dto;

import java.time.LocalDate;
import java.util.UUID;

public record RegistrarFornecimentoCommand(
        UUID loteId,
        LocalDate dataFornecimento,
        Double quantidadeKg,
        Double teorMateriaSeca
) {
    public RegistrarFornecimentoCommand {
        if (loteId == null) throw new IllegalArgumentException("O lote e obrigatorio.");
        if (dataFornecimento == null) throw new IllegalArgumentException("A data de fornecimento e obrigatoria.");
        if (quantidadeKg == null || quantidadeKg <= 0.0) throw new IllegalArgumentException("A quantidade fornecida deve ser maior que zero.");
        if (teorMateriaSeca == null || teorMateriaSeca <= 0.0 || teorMateriaSeca > 100.0) {
            throw new IllegalArgumentException("O teor de materia seca deve estar entre 0.1% e 100%.");
        }
    }
}
