package com.br.usecase.port;

import java.time.LocalDate;
import java.util.UUID;

public interface FornecimentoRacaoRepositoryPort {
    void salvar(UUID loteId, LocalDate dataFornecimento, Double quantidadeKg, Double teorMateriaSeca);
    Double somarConsumoMateriaSecaPorLoteNoPeriodo(UUID loteId, LocalDate inicio, LocalDate fim);
}
