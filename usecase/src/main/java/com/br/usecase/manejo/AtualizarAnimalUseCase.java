package com.br.usecase.manejo;

import com.br.core.domain.model.Animal;
import com.br.core.domain.repository.AnimalRepository;
import com.br.usecase.dto.AtualizarAnimalCommand;
import jakarta.inject.Named;
import jakarta.transaction.Transactional;

@Named
public class AtualizarAnimalUseCase {

    private final AnimalRepository animalRepository;

    public AtualizarAnimalUseCase(AnimalRepository animalRepository) {
        this.animalRepository = animalRepository;
    }

    @Transactional
    public void executar(AtualizarAnimalCommand command) {
        Animal animal = animalRepository.buscarPorId(command.animalId())
                .orElseThrow(() -> new IllegalArgumentException("Animal nao encontrado."));

        animalRepository.buscarPorBrinco(command.brincoRgd())
                .filter(encontrado -> !encontrado.getId().equals(animal.getId()))
                .ifPresent(encontrado -> {
                    throw new IllegalArgumentException("Erro: Já existe um animal com o brinco " + command.brincoRgd());
                });

        animal.atualizarDadosCadastrais(
                command.brincoRgd(),
                command.dataNascimento(),
                command.sexo(),
                command.categoria()
        );
        animalRepository.salvar(animal);
    }
}
