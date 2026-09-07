package com.br.usecase.manejo;

import com.br.core.domain.enums.CategoriaDespesa;
import com.br.core.domain.enums.Status;
import com.br.core.domain.enums.TipoDeCusto;
import com.br.core.domain.model.Animal;
import com.br.core.domain.model.Despesa;
import com.br.core.domain.repository.AnimalRepository;
import com.br.core.domain.repository.DespesaRepository;
import com.br.usecase.dto.RegistrarCompraAnimalCommand;
import jakarta.inject.Named;
import jakarta.transaction.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Named
public class RegistrarCompraAnimalUseCase {

    private final AnimalRepository animalRepository;
    private final DespesaRepository despesaRepository;

    public RegistrarCompraAnimalUseCase(AnimalRepository animalRepository, DespesaRepository despesaRepository) {
        this.animalRepository = animalRepository;
        this.despesaRepository = despesaRepository;
    }

    @Transactional
    public UUID executar(RegistrarCompraAnimalCommand command) {
        if (command.brincoRgd() == null || command.brincoRgd().isBlank()) {
            throw new IllegalArgumentException("O brinco/RGD é obrigatório.");
        }
        if (command.dataNascimento() == null) {
            throw new IllegalArgumentException("A data de nascimento é obrigatória.");
        }
        if (command.sexo() == null) {
            throw new IllegalArgumentException("O sexo é obrigatório.");
        }
        if (command.categoria() == null) {
            throw new IllegalArgumentException("A categoria é obrigatória.");
        }
        if (command.valorCompra() != null && command.valorCompra().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("O valor da compra deve ser maior que zero.");
        }
        if (command.valorCompra() != null && command.dataCompra() == null) {
            throw new IllegalArgumentException("A data da compra é obrigatória quando o valor da compra é informado.");
        }

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
                command.loteId()
        );

        animalRepository.salvar(animal);
        registrarDespesaDeCompra(command, animal);
        return animal.getId();
    }

    private void registrarDespesaDeCompra(RegistrarCompraAnimalCommand command, Animal animal) {
        if (command.valorCompra() == null) {
            return;
        }

        Despesa despesa = new Despesa(
                "Compra do animal " + animal.getBrincoRgd(),
                command.valorCompra(),
                command.dataCompra(),
                CategoriaDespesa.COMPRA_ANIMAL,
                TipoDeCusto.ANIMAL,
                animal.getId()
        );
        despesaRepository.salvar(despesa);
    }
}
