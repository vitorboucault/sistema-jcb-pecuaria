package com.sistema.sistemajcb.infrastructure.persistence.adapter;

import com.sistema.sistemajcb.domain.model.EventoReprodutivo;
import com.sistema.sistemajcb.domain.repository.EventoReprodutivoRepository;
import com.sistema.sistemajcb.infrastructure.persistence.mapper.EventoReprodutivoMapper;
import com.sistema.sistemajcb.infrastructure.persistence.repository.SpringDataEventoReprodutivoRepository;

import java.util.UUID;

public class EventoReprodutivoRepositoryImpl implements EventoReprodutivoRepository {

    private final SpringDataEventoReprodutivoRepository springData;
    private final EventoReprodutivoMapper mapper;

    public EventoReprodutivoRepositoryImpl(SpringDataEventoReprodutivoRepository springData, EventoReprodutivoMapper mapper) {
        this.springData = springData;
        this.mapper = mapper;
    }

    @Override
    public void salvar(EventoReprodutivo evento) {
        springData.save(mapper.toEntity(evento));
    }

    @Override
    public long contarFemeasUnicasNaEstacao(UUID estacaoMontaId) {
        return springData.countDistinctAnimalIdByEstacaoMontaId(estacaoMontaId);
    }

}
