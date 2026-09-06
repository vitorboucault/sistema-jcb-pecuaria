package com.br.usecase.manejo;

import com.br.usecase.dto.RegistrarVendaCommand;
import com.br.core.domain.model.Animal;
import com.br.core.domain.enums.ModalidadeVenda;
import com.br.core.domain.enums.Status;
import com.br.core.domain.model.VendaAnimal;
import com.br.core.domain.repository.AnimalRepository;
import com.br.core.domain.repository.VendaAnimalRepository;
import jakarta.transaction.Transactional;
import jakarta.inject.Named;

import java.util.UUID;

@Named
public class RegistrarVendaAnimalUseCase {
    private final VendaAnimalRepository vendaRepository;
    private final AnimalRepository animalRepository;

    public RegistrarVendaAnimalUseCase(VendaAnimalRepository vendaRepository, AnimalRepository animalRepository) {
        this.vendaRepository = vendaRepository;
        this.animalRepository = animalRepository;
    }

    @Transactional
    public UUID executar(RegistrarVendaCommand command) {
        Animal animal = animalRepository.buscarPorId(command.animalId())
                .orElseThrow(() -> new IllegalArgumentException("Animal nao encontrado."));

        if (animal.getStatus() != Status.ATIVO) {
            throw new IllegalStateException("Apenas animais ATIVOS podem ser vendidos.");
        }

        if (command.modalidade() == ModalidadeVenda.FRIGORIFICO &&
                (command.rendimentoCarcacaPercentual() == null || command.rendimentoCarcacaPercentual() <= 0)) {
            throw new IllegalArgumentException("Venda para frigorífico exige o rendimento de carcaça (ex: 52%).");
        }

        VendaAnimal venda = new VendaAnimal(
                UUID.randomUUID(),
                animal.getId(),
                command.dataVenda(),
                command.modalidade(),
                command.pesoVivoKg(),
                command.rendimentoCarcacaPercentual(),
                command.precoAcordado()
        );
        animal.registrarVenda();
        vendaRepository.salvar(venda);
        animalRepository.salvar(animal);
        return animal.getId();
    }
}