package com.sistema.sistemajcb.infrastructure.persistence.adapter;

import com.sistema.sistemajcb.domain.model.Desmame;
import com.sistema.sistemajcb.domain.repository.DesmameRepository;
import com.sistema.sistemajcb.infrastructure.persistence.mapper.DesmameMapper;
import com.sistema.sistemajcb.infrastructure.persistence.repository.SpringDataDesmameRepository;
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