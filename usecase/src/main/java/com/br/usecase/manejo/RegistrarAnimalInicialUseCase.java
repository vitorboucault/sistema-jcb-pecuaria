package com.br.usecase.manejo;

import com.br.core.domain.enums.Status;
import com.br.core.domain.model.Animal;
import com.br.core.domain.repository.AnimalRepository;
import com.br.usecase.dto.RegistrarAnimalInicialCommand;
import jakarta.inject.Named;
import jakarta.transaction.Transactional;

import java.time.LocalDate;
import java.util.UUID;

@Named
public class RegistrarAnimalInicialUseCase {

    private final AnimalRepository animalRepository;

    public RegistrarAnimalInicialUseCase(AnimalRepository animalRepository) {
        this.animalRepository = animalRepository;
    }

    @Transactional
    public UUID executar(RegistrarAnimalInicialCommand command) {
        validar(command);

        if (animalRepository.buscarPorBrinco(command.brincoRgd()).isPresent()) {
            throw new IllegalArgumentException("Erro: Já existe um animal com o brinco " + command.brincoRgd());
        }

        Animal animal = new Animal(
                UUID.randomUUID(),
                command.brincoRgd(),
                command.dataNascimento(),
                command.sexo(),
                command.categoria(),
                Status.ATIVO,
                null,
                command.loteId(),
                null,
                command.origem()
        );

        animalRepository.salvar(animal);
        return animal.getId();
    }

    private void validar(RegistrarAnimalInicialCommand command) {
        if (command.brincoRgd() == null || command.brincoRgd().isBlank()) {
            throw new IllegalArgumentException("O brinco/RGD é obrigatório.");
        }
        if (command.dataNascimento() == null) {
            throw new IllegalArgumentException("A data de nascimento é obrigatória.");
        }
        if (command.dataNascimento().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("A data de nascimento não pode ser futura.");
        }
        if (command.sexo() == null) {
            throw new IllegalArgumentException("O sexo é obrigatório.");
        }
        if (command.categoria() == null) {
            throw new IllegalArgumentException("A categoria é obrigatória.");
        }
        if (command.origem() == null) {
            throw new IllegalArgumentException("A origem é obrigatória.");
        }
    }
}
