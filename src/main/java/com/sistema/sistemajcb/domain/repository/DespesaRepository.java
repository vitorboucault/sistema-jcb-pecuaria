package com.sistema.sistemajcb.domain.repository;

import com.sistema.sistemajcb.domain.model.Despesa;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface DespesaRepository {
    void salvar(Despesa despesa);
    List<Despesa> buscarPorLote(UUID loteId);
    List<Despesa> buscarPorPeriodo(LocalDate inicio, LocalDate fim);
}
