package com.sistema.sistemajcb.domain.repository;

import com.sistema.sistemajcb.domain.model.Pesagem;

import java.util.Optional;
import java.util.UUID;
import java.util.List;

public interface PesagemRepository {
    void salvar(Pesagem pesagem);
    List<Pesagem> buscarPesagemPorId(UUID id);
    Optional<Pesagem> buscarUltimaPesagemDoAnimal(UUID animalId);
    List<Pesagem> buscarHistoricoPorAnimal(UUID id);
}
