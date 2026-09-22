package com.br.usecase.manejo;

import com.br.core.domain.enums.Status;
import com.br.core.domain.enums.OrigemPesagem;
import com.br.core.domain.model.Animal;
import com.br.core.domain.model.Pesagem;
import com.br.core.domain.repository.AnimalRepository;
import com.br.core.domain.repository.PesagemRepository;
import com.br.usecase.dto.RegistrarAnimalInicialCommand;
import jakarta.inject.Named;
import jakarta.transaction.Transactional;

import java.time.LocalDate;
import java.util.UUID;

@Named
public class RegistrarAnimalInicialUseCase {

    private final AnimalRepository animalRepository;
    private final PesagemRepository pesagemRepository;

    public RegistrarAnimalInicialUseCase(
            AnimalRepository animalRepository,
            PesagemRepository pesagemRepository
    ) {
        this.animalRepository = animalRepository;
        this.pesagemRepository = pesagemRepository;
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

        Pesagem pesagemInicial = criarPesagemInicial(animal.getId(), command);
        animalRepository.salvar(animal);
        if (pesagemInicial != null) {
            pesagemRepository.salvar(pesagemInicial);
        }
        return animal.getId();
    }

    private Pesagem criarPesagemInicial(UUID animalId, RegistrarAnimalInicialCommand command) {
        boolean pesoInformado = command.pesoAtual() != null;
        boolean dataPesagemInformada = command.dataPesagem() != null;

        if (pesoInformado != dataPesagemInformada) {
            throw new IllegalArgumentException("O peso atual e a data da pesagem devem ser informados juntos.");
        }
        if (!pesoInformado) {
            return null;
        }
        if (command.pesoAtual() <= 0) {
            throw new IllegalArgumentException("O peso atual deve ser maior que zero.");
        }
        if (command.dataPesagem().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("A data da pesagem não pode ser futura.");
        }
        if (command.dataPesagem().isBefore(command.dataNascimento())) {
            throw new IllegalArgumentException("A data da pesagem não pode ser anterior à data de nascimento.");
        }

        return new Pesagem(
                animalId,
                command.dataPesagem(),
                command.pesoAtual(),
                true,
                OrigemPesagem.CADASTRO_INICIAL
        );
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
