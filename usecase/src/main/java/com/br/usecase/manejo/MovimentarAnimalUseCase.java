package com.br.usecase.manejo;

import com.br.core.domain.model.Animal;
import com.br.core.domain.repository.AnimalRepository;
import com.br.core.domain.repository.LoteRepository;
import jakarta.transaction.Transactional;
import jakarta.inject.Named;

import java.util.UUID;

@Named
public class MovimentarAnimalUseCase {
    private final AnimalRepository animalRepository;
    private final LoteRepository loteRepository;

    public MovimentarAnimalUseCase(AnimalRepository animalRepository, LoteRepository loteRepository) {
        this.animalRepository = animalRepository;
        this.loteRepository = loteRepository;
    }

    @Transactional
    public void executar(UUID animalId, UUID novoLoteId) {
        Animal animal = animalRepository.buscarPorId(animalId)
                .orElseThrow(() -> new IllegalArgumentException("Animal nao encontrado."));

        loteRepository.buscarPorId(novoLoteId)
                .orElseThrow(() -> new IllegalArgumentException("Lote de destino nao existe."));

        animal.transferirParaLote(novoLoteId);
        animalRepository.salvar(animal);
    }
}
