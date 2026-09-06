package com.br.core.domain.repository;

import com.br.core.domain.model.MovimentacaoLote;


import java.util.List;
import java.util.UUID;

public interface MovimentacaoLoteRepository {
    void salvar(MovimentacaoLote movimentacao);
    List<MovimentacaoLote> buscarLotesAtivosNoPasto(UUID pastoId);
}
