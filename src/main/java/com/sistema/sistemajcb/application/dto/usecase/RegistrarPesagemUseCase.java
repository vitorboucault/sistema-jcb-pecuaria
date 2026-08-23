package com.sistema.sistemajcb.application.dto.usecase;

import com.sistema.sistemajcb.application.dto.RegistrarPesagemCommand;
import com.sistema.sistemajcb.application.dto.RegistrarPesagemResult;
import com.sistema.sistemajcb.domain.model.Pesagem;
import com.sistema.sistemajcb.domain.repository.PesagemRepository;
import com.sistema.sistemajcb.domain.service.CalculadoraGmdService;
import com.sistema.sistemajcb.domain.repository.AnimalRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
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

        if (animalRepository.buscarPorId(command.animalId()).isEmpty()) {
            throw new IllegalArgumentException("Erro: Animal não encontrado no sistema.");
        }

        Optional<Pesagem> ultimaPesagem = pesagemRepository.buscarUltimaPesagemDoAnimal(command.animalId());

        Pesagem novaPesagem = new Pesagem(
                command.animalId(),
                command.dataPesagem(),
                command.pesoKg(),
                command.jejum()
        );

        double gmd = 0.0;
        if (ultimaPesagem.isPresent()) {
            gmd = calculadora.calcularGmd(ultimaPesagem.get(), novaPesagem);
        }
        pesagemRepository.salvar(novaPesagem);

        return new RegistrarPesagemResult(novaPesagem.getId(), novaPesagem.getPesoKg(), gmd);
    }
}
