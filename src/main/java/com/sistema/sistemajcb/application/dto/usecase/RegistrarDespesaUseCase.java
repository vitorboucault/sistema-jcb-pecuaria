package com.sistema.sistemajcb.application.dto.usecase;

import com.sistema.sistemajcb.application.dto.RegistrarDespesaCommand;
import com.sistema.sistemajcb.domain.enums.TipoDeCusto;
import com.sistema.sistemajcb.domain.model.Despesa;
import com.sistema.sistemajcb.domain.repository.DespesaRepository;
import com.sistema.sistemajcb.domain.repository.LoteRepository;
import com.sistema.sistemajcb.domain.repository.PastoRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
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
                .orElseThrow(() -> new IllegalArgumentException("Lote não encontrado para alocação de custo."));
    } else if (command.tipoDeCusto() == TipoDeCusto.PASTO) {
        pastoRepository.buscarPorId(command.referenciaId())
                .orElseThrow(() -> new IllegalArgumentException("Pasto não encontrado para alocação de custo."));
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
