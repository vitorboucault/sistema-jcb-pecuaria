package com.br.usecase.manejo;

import com.br.core.domain.model.Animal;
import com.br.core.domain.repository.AnimalRepository;
import com.br.usecase.dto.RegistrarMorteAnimalCommand;
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
    public void executar(RegistrarMorteAnimalCommand command) {
        Animal animal = animalRepository.buscarPorId(command.animalId())
                .orElseThrow(() -> new IllegalArgumentException("Animal nao encontrado."));
        animal.registrarMorte(command.dataMorte());
        animalRepository.salvar(animal);
    }
}
