package com.br.core.domain.repository;

import com.br.core.domain.model.VendaAnimal;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public interface VendaAnimalRepository {
    void salvar(VendaAnimal venda);
    BigDecimal somarReceitasNoPeriodo(LocalDate inicio, LocalDate fim);
    boolean existePorAnimalId(UUID animalId);
}
