package com.br.core.domain.repository;

import com.br.core.domain.model.Pesagem;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;
import java.util.List;

public interface PesagemRepository {
    void salvar(Pesagem pesagem);
    List<Pesagem> buscarPesagemPorId(UUID id);
    Optional<Pesagem> buscarUltimaPesagemDoAnimal(UUID animalId);
    List<Pesagem> buscarHistoricoPorAnimal(UUID id);
    boolean existePorAnimalId(UUID animalId);

    Double calcularGanhoPesoTotalNoPeriodo(LocalDate inicioSafra, LocalDate fimSafra);
}
