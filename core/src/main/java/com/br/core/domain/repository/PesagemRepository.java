package com.br.core.domain.repository;

import com.br.core.domain.model.Pesagem;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;
import java.util.List;
import java.util.Map;

public interface PesagemRepository {
    void salvar(Pesagem pesagem);
    void excluirPorId(UUID id);
    List<Pesagem> buscarPesagemPorId(UUID id);
    Optional<Pesagem> buscarUltimaPesagemDoAnimal(UUID animalId);
    Map<UUID, Pesagem> buscarUltimasPesagensPorAnimalIds(List<UUID> animalIds);
    List<Pesagem> buscarHistoricoPorAnimal(UUID id);
    boolean existePorAnimalId(UUID animalId);

    Double calcularGanhoPesoTotalNoPeriodo(LocalDate inicioSafra, LocalDate fimSafra);
}
