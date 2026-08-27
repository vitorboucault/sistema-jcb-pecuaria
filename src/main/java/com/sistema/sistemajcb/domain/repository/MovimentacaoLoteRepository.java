package com.sistema.sistemajcb.domain.repository;

import com.sistema.sistemajcb.domain.model.MovimentacaoLote;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public interface MovimentacaoLoteRepository {
    void salvar(MovimentacaoLote movimentacao);
    List<MovimentacaoLote> buscarLotesAtivosNoPasto(UUID pastoId);
}
