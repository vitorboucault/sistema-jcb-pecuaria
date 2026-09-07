package com.br.usecase.manejo;

import com.br.usecase.dto.RegistrarPesagemCommand;
import com.br.usecase.dto.RegistrarPesagemResult;
import com.br.core.domain.enums.Status;
import com.br.core.domain.model.Animal;
import com.br.core.domain.model.Pesagem;
import com.br.core.domain.repository.PesagemRepository;
import com.br.core.domain.service.CalculadoraGmdService;
import com.br.core.domain.repository.AnimalRepository;
import jakarta.transaction.Transactional;
import jakarta.inject.Named;

import java.util.Optional;

@Named
public class RegistrarPesagemUseCase {
    private final AnimalRepository animalRepository;
    private final PesagemRepository pesagemRepository;

    private final CalculadoraGmdService calculadora = new CalculadoraGmdService();

    public RegistrarPesagemUseCase(AnimalRepository animalRepository, PesagemRepository pesagemRepository) {
        this.animalRepository = animalRepository;
        this.pesagemRepository = pesagemRepository;
    }

    @Transactional
    public RegistrarPesagemResult executar(RegistrarPesagemCommand command) {

        Animal animal = animalRepository.buscarPorId(command.animalId())
                .orElseThrow(() -> new IllegalArgumentException("Erro: Animal nao encontrado no sistema."));

        if (animal.getStatus() != Status.ATIVO) {
            throw new IllegalStateException("Somente animais ativos podem receber pesagem.");
        }

        Optional<Pesagem> ultimaPesagem = pesagemRepository.buscarUltimaPesagemDoAnimal(command.animalId());

        Pesagem novaPesagem = new Pesagem(
                command.animalId(),
                command.dataPesagem(),
                command.pesoKg(),
                command.jejum(),
                command.origem()
        );

        double gmd = 0.0;
        if (ultimaPesagem.isPresent()) {
            gmd = calculadora.calcularGmd(ultimaPesagem.get(), novaPesagem);
        }
        pesagemRepository.salvar(novaPesagem);

        return new RegistrarPesagemResult(novaPesagem.getId(), novaPesagem.getPeso(), gmd);
    }
}
