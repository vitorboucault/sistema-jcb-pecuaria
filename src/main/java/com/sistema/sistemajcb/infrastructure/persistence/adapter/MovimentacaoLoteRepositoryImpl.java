package com.sistema.sistemajcb.infrastructure.persistence.adapter;

import com.sistema.sistemajcb.domain.model.MovimentacaoLote;
import com.sistema.sistemajcb.domain.repository.MovimentacaoLoteRepository;
import com.sistema.sistemajcb.infrastructure.persistence.mapper.MovimentacaoLoteMapper;
import com.sistema.sistemajcb.infrastructure.persistence.repository.SpringDataMovimentacaoLoteRepository;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class MovimentacaoLoteRepositoryImpl implements MovimentacaoLoteRepository {
    private final SpringDataMovimentacaoLoteRepository springData;
    private final MovimentacaoLoteMapper mapper;

    public MovimentacaoLoteRepositoryImpl(SpringDataMovimentacaoLoteRepository springData, MovimentacaoLoteMapper mapper) {
        this.springData = springData;
        this.mapper = mapper;
    }

    @Override
    public void salvar(MovimentacaoLote movimentacao) {
        springData.save(mapper.toEntity(movimentacao));
    }

    @Override
    public List<MovimentacaoLote> buscarLotesAtivosNoPasto(UUID pastoId) {
        return springData.findByPastoIdAndDataSaidaIsNull(pastoId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

}
