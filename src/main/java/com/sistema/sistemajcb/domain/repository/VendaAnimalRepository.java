package com.sistema.sistemajcb.domain.repository;

import com.sistema.sistemajcb.domain.model.VendaAnimal;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface VendaAnimalRepository {
    void salvar(VendaAnimal venda);
    BigDecimal somarReceitasNoPeriodo(LocalDate inicio, LocalDate fim);
}
