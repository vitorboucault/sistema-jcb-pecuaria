package com.sistema.sistemajcb.infrastructure.persistence.adapter;

import com.sistema.sistemajcb.domain.model.Despesa;
import com.sistema.sistemajcb.domain.repository.DespesaRepository;
import com.sistema.sistemajcb.infrastructure.persistence.entity.DespesaEntity;
import com.sistema.sistemajcb.infrastructure.persistence.mapper.DespesaMapper;
import com.sistema.sistemajcb.infrastructure.persistence.repository.SpringDataDespesaRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class DespesaRepositoryImpl implements DespesaRepository {

    private final SpringDataDespesaRepository springDataRepository;
    private final DespesaMapper mapper;

    public DespesaRepositoryImpl(SpringDataDespesaRepository springDataRepository, DespesaMapper mapper) {
        this.springDataRepository = springDataRepository;
        this.mapper = mapper;
    }

    @Override
    public void salvar(Despesa despesa) {
        DespesaEntity entity = mapper.toEntity(despesa);
        springDataRepository.save(entity);
    }

    @Override
    public List<Despesa> buscarPorLote(UUID loteId) {
        return springDataRepository.findByTipoAndCentroCustoId("DESPESA", loteId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Despesa> buscarPorPeriodo(LocalDate inicio, LocalDate fim) {
        return springDataRepository.findByTipoAndDataTransacaoBetween("DESPESA", inicio, fim).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
}
