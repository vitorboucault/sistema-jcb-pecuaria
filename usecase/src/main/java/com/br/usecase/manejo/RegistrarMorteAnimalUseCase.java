package com.br.usecase.manejo;

import com.br.core.domain.model.Animal;
import com.br.core.domain.repository.AnimalRepository;
import jakarta.transaction.Transactional;
import jakarta.inject.Named;

import java.util.UUID;

@Named
public class RegistrarMorteAnimalUseCase {
    private final AnimalRepository animalRepository;

    public RegistrarMorteAnimalUseCase(AnimalRepository animalRepository) {
        this.animalRepository = animalRepository;
    }

    @Transactional
    public void executar(UUID animalId) {
        Animal animal = animalRepository.buscarPorId(animalId)
                .orElseThrow(() -> new IllegalArgumentException("Animal nao encontrado."));
        animal.registrarMorte();
        animalRepository.salvar(animal);
    }
}