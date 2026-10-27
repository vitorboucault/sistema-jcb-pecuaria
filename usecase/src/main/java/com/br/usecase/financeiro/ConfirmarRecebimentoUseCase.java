package com.br.usecase.financeiro;

import com.br.core.domain.model.LancamentoFinanceiro;
import com.br.core.domain.repository.LancamentoFinanceiroRepository;
import com.br.usecase.exception.LancamentoFinanceiroNaoEncontradoException;
import jakarta.inject.Named;
import jakarta.transaction.Transactional;

import java.time.LocalDate;
import java.util.UUID;

@Named
public class ConfirmarRecebimentoUseCase {
    private final LancamentoFinanceiroRepository repository;

    public ConfirmarRecebimentoUseCase(LancamentoFinanceiroRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public LancamentoFinanceiro executar(UUID id, LocalDate dataEfetivacao) {
        LancamentoFinanceiro original = repository.buscarPorIdParaAtualizacao(id)
                .orElseThrow(LancamentoFinanceiroNaoEncontradoException::new);
        LancamentoFinanceiro confirmado = original.confirmarRecebimento(dataEfetivacao);
        if (confirmado != original) {
            repository.salvar(confirmado);
        }
        return confirmado;
    }
}
