package com.br.usecase.manejo;

import com.br.core.domain.model.Animal;
import com.br.core.domain.repository.AnimalRepository;
import jakarta.transaction.Transactional;
import jakarta.inject.Named;

import java.util.List;

@Named
public class EvoluirCategoriaUseCase {
    private final AnimalRepository animalRepository;

    public EvoluirCategoriaUseCase(AnimalRepository animalRepository) {
        this.animalRepository = animalRepository;
    }

    @Transactional
    public void executar(){
        List<Animal> animaisElegiveis = animalRepository.buscarAnimaisElegiveisParaEvolucao();
        int atualizados = 0;
        for (Animal animal : animaisElegiveis) {
            if (animal.avaliarEvolucaoPorIdade()) {
                animalRepository.salvar(animal);
                atualizados++;
            }
        }
        System.out.println("Job Executado: " + atualizados + " animais evoluíram de categoria nesta madrugada.");
    }


}
