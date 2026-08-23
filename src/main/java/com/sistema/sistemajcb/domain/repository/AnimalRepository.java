package com.sistema.sistemajcb.domain.repository;

import com.sistema.sistemajcb.domain.Animal;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AnimalRepository {

    void salvar(Animal animal);
    Optional<Animal> buscarPorId(UUID id);
    Optional<Animal> buscarPorBrinco(String brinco);
    List<Animal> buscarAnimaisElegiveisParaEvolucao();

    List<Animal> buscarPorLote(UUID loteId);
    long contarAnimaisAtivos();
}
