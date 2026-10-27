package com.br.application.dto;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record ConfirmarLancamentoFinanceiroRequest(@NotNull LocalDate dataEfetivacao) {
    @JsonAnySetter
    public void rejeitarCampoDesconhecido(String campo, Object valor) {
        throw new IllegalArgumentException("A confirmação aceita somente dataEfetivacao.");
    }
}
