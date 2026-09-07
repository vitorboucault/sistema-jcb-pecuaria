package com.br.usecase.manejo;

import com.br.core.domain.enums.CategoriaDespesa;
import com.br.core.domain.enums.Status;
import com.br.core.domain.model.Animal;
import com.br.core.domain.model.Despesa;
import com.br.core.domain.model.Pesagem;
import com.br.core.domain.repository.AnimalRepository;
import com.br.core.domain.repository.DespesaRepository;
import com.br.core.domain.repository.DiagnosticoGestacaoRepository;
import com.br.core.domain.repository.EventoReprodutivoRepository;
import com.br.core.domain.repository.PesagemRepository;
import com.br.core.domain.repository.VendaAnimalRepository;
import jakarta.inject.Named;
import jakarta.transaction.Transactional;

import java.util.List;
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

        List<Pesagem> pesagens = pesagemRepository.buscarHistoricoPorAnimal(animalId);
        List<Despesa> despesas = despesaRepository.buscarPorAnimal(animalId);

        if (possuiHistoricoOperacional(animalId, pesagens, despesas)) {
            throw new IllegalStateException("Animal possui historico ou vinculos e nao pode ser excluido.");
        }

        removerRegistrosIniciais(pesagens, despesas);
        animalRepository.excluirPorId(animalId);
    }

    private boolean possuiHistoricoOperacional(UUID animalId, List<Pesagem> pesagens, List<Despesa> despesas) {
        return pesagens.size() > 1
                || despesas.stream().anyMatch(this::naoEhDespesaInicialDeCompra)
                || vendaAnimalRepository.existePorAnimalId(animalId)
                || eventoReprodutivoRepository.existePorAnimalId(animalId)
                || diagnosticoGestacaoRepository.existePorAnimalId(animalId);
    }

    private boolean naoEhDespesaInicialDeCompra(Despesa despesa) {
        return despesa.getCategoria() != CategoriaDespesa.COMPRA_ANIMAL;
    }

    private void removerRegistrosIniciais(List<Pesagem> pesagens, List<Despesa> despesas) {
        pesagens.forEach(pesagem -> pesagemRepository.excluirPorId(pesagem.getId()));
        despesas.forEach(despesa -> despesaRepository.excluirPorId(despesa.getId()));
    }
}
