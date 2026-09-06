package com.br.usecase.nutricao;

import com.br.core.domain.repository.LoteRepository;
import com.br.usecase.dto.RegistrarFornecimentoCommand;
import com.br.usecase.port.FornecimentoRacaoRepositoryPort;
import jakarta.inject.Named;
import jakarta.transaction.Transactional;

@Named
public class RegistrarFornecimentoRacaoUseCase {

    private final FornecimentoRacaoRepositoryPort fornecimentoRepository;
    private final LoteRepository loteRepository;

    public RegistrarFornecimentoRacaoUseCase(FornecimentoRacaoRepositoryPort fornecimentoRepository, LoteRepository loteRepository) {
        this.fornecimentoRepository = fornecimentoRepository;
        this.loteRepository = loteRepository;
    }

    @Transactional
    public void executar(RegistrarFornecimentoCommand command) {
        loteRepository.buscarPorId(command.loteId())
                .orElseThrow(() -> new IllegalArgumentException("Lote informado nao foi encontrado."));

        fornecimentoRepository.salvar(
                command.loteId(),
                command.dataFornecimento(),
                command.quantidadeKg(),
                command.teorMateriaSeca()
        );
    }
}
