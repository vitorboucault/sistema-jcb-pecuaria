package com.sistema.sistemajcb.application.dto.usecase;

import com.sistema.sistemajcb.application.dto.RegistrarNascimentoCommand;
import com.sistema.sistemajcb.domain.Animal;
import com.sistema.sistemajcb.domain.enums.Sexo;
import com.sistema.sistemajcb.domain.repository.AnimalRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class RegistrarNascimentoUseCase {
    private final AnimalRepository animalRepository;

    public RegistrarNascimentoUseCase(AnimalRepository animalRepository) {
        this.animalRepository = animalRepository;
    }
    @Transactional
    public void executar(RegistrarNascimentoCommand command){
        if (animalRepository.buscarPorBrinco(command.brincoRgd()).isPresent()) {
            throw new IllegalArgumentException("Erro: Já existe um animal com o brinco " + command.brincoRgd());
        }
        UUID loteDaMae = null;

        if (command.maeId() != null) {
            Animal mae = animalRepository.buscarPorId(command.maeId())
                    .orElseThrow(() -> new IllegalArgumentException("Matriz não encontrada no sistema."));

            if (mae.getSexo() != Sexo.FEMEA) {
                throw new IllegalArgumentException("O animal vinculado como mãe não é uma fêmea.");
            }
            loteDaMae = mae.getLoteId();
        }

        Animal bezerro = new Animal(
                command.brincoRgd(),
                command.dataNascimento(),
                command.sexo(),
                command.maeId(),
                loteDaMae
        );
        animalRepository.salvar(bezerro);
    }
}
