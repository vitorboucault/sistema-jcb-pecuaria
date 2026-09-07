package com.br.infra.persistence.adapter;

import com.br.core.domain.model.EventoReprodutivo;
import com.br.core.domain.repository.EventoReprodutivoRepository;
import com.br.infra.persistence.mapper.EventoReprodutivoMapper;
import com.br.infra.persistence.repository.SpringDataEventoReprodutivoRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
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

    @Override
    public boolean existePorAnimalId(UUID animalId) {
        return springData.existsByAnimalIdOrTouroId(animalId, animalId);
    }

}
