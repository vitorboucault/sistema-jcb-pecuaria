package com.br.usecase.financeiro;

import com.br.usecase.dto.RegistrarDespesaCommand;
import com.br.core.domain.enums.TipoDeCusto;
import com.br.core.domain.model.Despesa;
import com.br.core.domain.repository.DespesaRepository;
import com.br.core.domain.repository.LoteRepository;
import com.br.core.domain.repository.PastoRepository;
import jakarta.transaction.Transactional;
import jakarta.inject.Named;

@Named
public class RegistrarDespesaUseCase {
    private final DespesaRepository despesaRepository;
    private final LoteRepository loteRepository;
    private final PastoRepository pastoRepository;

    public RegistrarDespesaUseCase(DespesaRepository despesaRepository, LoteRepository loteRepository, PastoRepository pastoRepository) {
        this.despesaRepository = despesaRepository;
        this.loteRepository = loteRepository;
        this.pastoRepository = pastoRepository;
    }

    @Transactional
    public void executar(RegistrarDespesaCommand command){
    if (command.tipoDeCusto() == TipoDeCusto.LOTE) {
        loteRepository.buscarPorId(command.referenciaId())
                .orElseThrow(() -> new IllegalArgumentException("Lote nao encontrado para alocaçao de custo."));
    } else if (command.tipoDeCusto() == TipoDeCusto.PASTO) {
        pastoRepository.buscarPorId(command.referenciaId())
                .orElseThrow(() -> new IllegalArgumentException("Pasto nao encontrado para alocaçao de custo."));
    }

    Despesa novaDespesa = new Despesa(
            command.descricao(),
            command.valor(),
            command.dataOcorrencia(),
            command.categoria(),
            command.tipoDeCusto(),
            command.referenciaId()
    );
        despesaRepository.salvar(novaDespesa);
}

}
