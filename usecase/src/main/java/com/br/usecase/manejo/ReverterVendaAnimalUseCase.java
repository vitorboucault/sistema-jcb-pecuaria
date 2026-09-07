package com.br.usecase.manejo;

import com.br.core.domain.enums.Status;
import com.br.core.domain.model.Animal;
import com.br.core.domain.model.VendaAnimal;
import com.br.core.domain.repository.AnimalRepository;
import com.br.core.domain.repository.VendaAnimalRepository;
import jakarta.inject.Named;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.UUID;

@Named
public class ReverterVendaAnimalUseCase {
    private final AnimalRepository animalRepository;
    private final VendaAnimalRepository vendaAnimalRepository;

    public ReverterVendaAnimalUseCase(AnimalRepository animalRepository, VendaAnimalRepository vendaAnimalRepository) {
        this.animalRepository = animalRepository;
        this.vendaAnimalRepository = vendaAnimalRepository;
    }

    @Transactional
    public void executar(UUID animalId) {
        Animal animal = animalRepository.buscarPorId(animalId)
                .orElseThrow(() -> new IllegalArgumentException("Animal nao encontrado."));

        if (animal.getStatus() != Status.VENDIDO) {
            throw new IllegalStateException("Somente animais vendidos podem ter a venda revertida.");
        }

        List<VendaAnimal> vendas = vendaAnimalRepository.buscarPorAnimalId(animalId);
        if (vendas.size() != 1) {
            throw new IllegalStateException("Venda do animal inconsistente.");
        }

        VendaAnimal venda = vendas.getFirst();
        animal.reverterVenda();
        vendaAnimalRepository.excluirPorId(venda.getId());
        animalRepository.salvar(animal);
    }
}
