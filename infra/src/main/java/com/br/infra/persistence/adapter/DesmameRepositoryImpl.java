package com.br.infra.persistence.adapter;

import com.br.core.domain.model.Desmame;
import com.br.core.domain.repository.DesmameRepository;
import com.br.infra.persistence.mapper.DesmameMapper;
import com.br.infra.persistence.repository.SpringDataDesmameRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class DesmameRepositoryImpl implements DesmameRepository {

    private final SpringDataDesmameRepository springData;
    private final DesmameMapper mapper;

    public DesmameRepositoryImpl(SpringDataDesmameRepository springData, DesmameMapper mapper) {
        this.springData = springData;
        this.mapper = mapper;
    }

    @Override
    public void salvar(Desmame desmame) {
        springData.save(mapper.toEntity(desmame));
    }

    @Override
    public long contarDesmamesPorEstacao(UUID estacaoMontaId) {
        return springData.countByEstacaoMontaId(estacaoMontaId);
    }
}