package com.br.usecase.financeiro;

import com.br.core.domain.model.LancamentoFinanceiro;
import com.br.core.domain.repository.AnimalRepository;
import com.br.core.domain.repository.LancamentoFinanceiroRepository;
import com.br.core.domain.repository.LoteRepository;
import com.br.core.domain.repository.PastoRepository;
import com.br.usecase.dto.RegistrarLancamentoFinanceiroCommand;
import jakarta.inject.Named;
import jakarta.transaction.Transactional;

@Named
public class RegistrarLancamentoFinanceiroUseCase {
    private final LancamentoFinanceiroRepository lancamentoRepository;
    private final AnimalRepository animalRepository;
    private final LoteRepository loteRepository;
    private final PastoRepository pastoRepository;

    public RegistrarLancamentoFinanceiroUseCase(LancamentoFinanceiroRepository lancamentoRepository,
                                                AnimalRepository animalRepository, LoteRepository loteRepository,
                                                PastoRepository pastoRepository) {
        this.lancamentoRepository = lancamentoRepository;
        this.animalRepository = animalRepository;
        this.loteRepository = loteRepository;
        this.pastoRepository = pastoRepository;
    }

    @Transactional
    public LancamentoFinanceiro executar(RegistrarLancamentoFinanceiroCommand command) {
        LancamentoFinanceiro lancamento = LancamentoFinanceiro.builder()
                .tipo(command.tipo()).descricao(command.descricao()).valor(command.valor())
                .categoria(command.categoria()).dataReferencia(command.dataReferencia())
                .dataVencimento(command.dataVencimento()).situacao(command.situacao())
                .dataEfetivacao(command.dataEfetivacao()).tipoDeCusto(command.tipoDeCusto())
                .referenciaId(command.referenciaId()).build();

        switch (lancamento.getTipoDeCusto()) {
            case GERAL -> { }
            case ANIMAL -> animalRepository.buscarPorId(lancamento.getReferenciaId())
                    .orElseThrow(() -> new IllegalArgumentException("Animal não encontrado para o centro de custo."));
            case LOTE -> loteRepository.buscarPorId(lancamento.getReferenciaId())
                    .orElseThrow(() -> new IllegalArgumentException("Lote não encontrado para o centro de custo."));
            case PASTO -> pastoRepository.buscarPorId(lancamento.getReferenciaId())
                    .orElseThrow(() -> new IllegalArgumentException("Pasto não encontrado para o centro de custo."));
        }
        lancamentoRepository.salvar(lancamento);
        return lancamento;
    }
}
