package com.br.usecase.manejo;

import com.br.core.domain.enums.Status;
import com.br.core.domain.model.Animal;
import com.br.core.domain.repository.AnimalRepository;
import com.br.core.domain.repository.DespesaRepository;
import com.br.core.domain.repository.DiagnosticoGestacaoRepository;
import com.br.core.domain.repository.EventoReprodutivoRepository;
import com.br.core.domain.repository.PesagemRepository;
import com.br.core.domain.repository.VendaAnimalRepository;
import jakarta.inject.Named;
import jakarta.transaction.Transactional;

import java.util.UUID;

@Named
public class ExcluirAnimalUseCase {

    private final AnimalRepository animalRepository;
    private final PesagemRepository pesagemRepository;
    private final DespesaRepository despesaRepository;
    private final VendaAnimalRepository vendaAnimalRepository;
    private final EventoReprodutivoRepository eventoReprodutivoRepository;
    private final DiagnosticoGestacaoRepository diagnosticoGestacaoRepository;

    public ExcluirAnimalUseCase(
            AnimalRepository animalRepository,
            PesagemRepository pesagemRepository,
            DespesaRepository despesaRepository,
            VendaAnimalRepository vendaAnimalRepository,
            EventoReprodutivoRepository eventoReprodutivoRepository,
            DiagnosticoGestacaoRepository diagnosticoGestacaoRepository) {
        this.animalRepository = animalRepository;
        this.pesagemRepository = pesagemRepository;
        this.despesaRepository = despesaRepository;
        this.vendaAnimalRepository = vendaAnimalRepository;
        this.eventoReprodutivoRepository = eventoReprodutivoRepository;
        this.diagnosticoGestacaoRepository = diagnosticoGestacaoRepository;
    }

    @Transactional
    public void executar(UUID animalId) {
        Animal animal = animalRepository.buscarPorId(animalId)
                .orElseThrow(() -> new IllegalArgumentException("Animal nao encontrado."));

        if (animal.getStatus() == Status.MORTO) {
            throw new IllegalStateException("Animal morto nao pode ser excluido pela interface normal.");
        }

        if (possuiDependencias(animalId)) {
            throw new IllegalStateException("Animal possui historico ou vinculos e nao pode ser excluido.");
        }

        animalRepository.excluirPorId(animalId);
    }

    private boolean possuiDependencias(UUID animalId) {
        return pesagemRepository.existePorAnimalId(animalId)
                || despesaRepository.existePorAnimalId(animalId)
                || vendaAnimalRepository.existePorAnimalId(animalId)
                || eventoReprodutivoRepository.existePorAnimalId(animalId)
                || diagnosticoGestacaoRepository.existePorAnimalId(animalId);
    }
}
