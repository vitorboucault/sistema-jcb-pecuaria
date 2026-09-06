package com.br.infra.persistence.adapter;

import com.br.core.domain.model.MovimentacaoLote;
import com.br.core.domain.repository.MovimentacaoLoteRepository;
import com.br.infra.persistence.mapper.MovimentacaoLoteMapper;
import com.br.infra.persistence.repository.SpringDataMovimentacaoLoteRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
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
