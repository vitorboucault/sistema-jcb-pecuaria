package com.br.core.domain.repository;

import com.br.core.domain.model.Despesa;
import com.br.core.domain.model.Lote;
import com.br.core.domain.model.Pagina;
import jakarta.inject.Named;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface DespesaRepository {
    void salvar(Despesa despesa);
    List<Despesa> buscarPorLote(UUID loteId);
    List<Despesa> buscarPorPeriodo(LocalDate inicio, LocalDate fim);
    BigDecimal somarDespesasNoPeriodo(LocalDate inicio, LocalDate fim);
    Pagina<Despesa> buscarTodosPaginado(int pagina, int tamanho);
    boolean existePorAnimalId(UUID animalId);

}
