package com.sistema.sistemajcb.application.dto.usecase;

import com.sistema.sistemajcb.domain.Animal;
import com.sistema.sistemajcb.domain.repository.AnimalRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
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
