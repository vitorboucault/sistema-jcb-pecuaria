package com.br.usecase.manejo;

import com.br.core.domain.model.Animal;
import com.br.core.domain.repository.AnimalRepository;
import jakarta.inject.Named;
import jakarta.transaction.Transactional;

import java.util.UUID;

@Named
public class ReverterMorteAnimalUseCase {
    private final AnimalRepository animalRepository;

    public ReverterMorteAnimalUseCase(AnimalRepository animalRepository) {
        this.animalRepository = animalRepository;
    }

    @Transactional
    public void executar(UUID animalId) {
        Animal animal = animalRepository.buscarPorId(animalId)
                .orElseThrow(() -> new IllegalArgumentException("Animal nao encontrado."));

        animal.reverterMorte();
        animalRepository.salvar(animal);
    }
}
