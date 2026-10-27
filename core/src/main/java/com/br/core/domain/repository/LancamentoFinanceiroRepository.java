package com.br.core.domain.repository;

import com.br.core.domain.model.LancamentoFinanceiro;

import java.util.Optional;
import java.util.UUID;

public interface LancamentoFinanceiroRepository {
    void salvar(LancamentoFinanceiro lancamento);

    Optional<LancamentoFinanceiro> buscarPorId(UUID id);

    /** Requer transação ativa; mantém bloqueio exclusivo até o fim da transação. */
    Optional<LancamentoFinanceiro> buscarPorIdParaAtualizacao(UUID id);
}
