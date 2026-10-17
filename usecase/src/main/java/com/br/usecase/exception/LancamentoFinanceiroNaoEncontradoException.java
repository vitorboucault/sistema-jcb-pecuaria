package com.br.usecase.exception;

public class LancamentoFinanceiroNaoEncontradoException extends RuntimeException {
    public LancamentoFinanceiroNaoEncontradoException() {
        super("Lançamento financeiro não encontrado.");
    }
}
