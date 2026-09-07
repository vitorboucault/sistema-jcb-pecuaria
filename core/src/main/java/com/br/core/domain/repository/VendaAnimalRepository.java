package com.br.core.domain.repository;

import com.br.core.domain.model.VendaAnimal;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import java.util.List;

public interface VendaAnimalRepository {
    void salvar(VendaAnimal venda);
    BigDecimal somarReceitasNoPeriodo(LocalDate inicio, LocalDate fim);
    boolean existePorAnimalId(UUID animalId);
    List<VendaAnimal> buscarPorAnimalId(UUID animalId);
    void excluirPorId(UUID vendaId);
}
