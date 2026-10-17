package com.br.usecase.financeiro;

import com.br.core.domain.model.LancamentoFinanceiro;
import com.br.core.domain.repository.LancamentoFinanceiroRepository;
import com.br.usecase.exception.LancamentoFinanceiroNaoEncontradoException;
import jakarta.inject.Named;

import java.util.UUID;

@Named
public class ConsultarLancamentoFinanceiroUseCase {
    private final LancamentoFinanceiroRepository repository;

    public ConsultarLancamentoFinanceiroUseCase(LancamentoFinanceiroRepository repository) {
        this.repository = repository;
    }

    public LancamentoFinanceiro executar(UUID id) {
        return repository.buscarPorId(id).orElseThrow(LancamentoFinanceiroNaoEncontradoException::new);
    }
}
