package com.br.core.domain.repository;

import com.br.core.domain.model.AquisicaoAnimal;

import java.util.Optional;
import java.util.UUID;

public interface AquisicaoAnimalRepository {

    void salvar(AquisicaoAnimal aquisicao);

    Optional<AquisicaoAnimal> buscarPorAnimalId(UUID animalId);
}
