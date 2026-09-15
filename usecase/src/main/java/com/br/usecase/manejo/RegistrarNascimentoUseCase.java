package com.br.usecase.manejo;

import com.br.core.domain.enums.Categoria;
import com.br.core.domain.model.Animal;
import com.br.core.domain.enums.Sexo;
import com.br.core.domain.enums.Status;
import com.br.core.domain.repository.AnimalRepository;
import com.br.usecase.dto.RegistrarNascimentoCommand;
import jakarta.transaction.Transactional;
import jakarta.inject.Named;

import java.util.UUID;

@Named
public class RegistrarNascimentoUseCase {
    private final AnimalRepository animalRepository;

    public RegistrarNascimentoUseCase(AnimalRepository animalRepository) {
        this.animalRepository = animalRepository;
    }
    @Transactional
    public UUID executar(RegistrarNascimentoCommand command){
        if (animalRepository.buscarPorBrinco(command.brincoRgd()).isPresent()) {
            throw new IllegalArgumentException("Erro: Já existe um animal com o brinco " + command.brincoRgd());
        }
        UUID loteDaMae = command.loteInicial();

        if (command.maeId() != null) {
            Animal mae = animalRepository.buscarPorId(command.maeId())
                    .orElseThrow(() -> new IllegalArgumentException("Matriz nao encontrada no sistema."));
            if (mae.getSexo() != Sexo.FEMEA) {
                throw new IllegalArgumentException("O animal vinculado como mae nao é uma fêmea.");
            }
            if (mae.getStatus() != Status.ATIVO) {
                throw new IllegalArgumentException("O animal não está ativo");
            }
            if (mae.getCategoriaAtual() != Categoria.VACA
                    && mae.getCategoriaAtual() != Categoria.NOVILHA) {
                throw new IllegalArgumentException("O animal informado não é uma matriz elegível.");
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
        return bezerro.getId();
    }
}
