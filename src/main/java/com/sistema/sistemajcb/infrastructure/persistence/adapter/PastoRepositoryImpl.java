package com.sistema.sistemajcb.infrastructure.persistence.adapter;

import com.sistema.sistemajcb.domain.model.Pasto;
import com.sistema.sistemajcb.domain.repository.LoteRepository;
import com.sistema.sistemajcb.domain.repository.PastoRepository;
import com.sistema.sistemajcb.infrastructure.persistence.entity.PastoEntity;
import com.sistema.sistemajcb.infrastructure.persistence.mapper.PastoMapper;
import com.sistema.sistemajcb.infrastructure.persistence.repository.SpringDataLoteRepository;
import com.sistema.sistemajcb.infrastructure.persistence.repository.SpringDataPastoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PastoRepositoryImpl implements PastoRepository {

    private final SpringDataPastoRepository springDataRepository;
    private final PastoMapper mapper;

    public PastoRepositoryImpl(SpringDataPastoRepository springDataRepository, PastoMapper mapper) {
        this.springDataRepository = springDataRepository;
        this.mapper = mapper;
    }

    @Override
    public void salvar(Pasto pasto) {
        PastoEntity entity = mapper.toEntity(pasto);
        springDataRepository.save(entity);
    }

    @Override
    public Optional<Pasto> buscarPorId(UUID id) {
        return springDataRepository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public List<Pasto> buscarTodos() {
        return springDataRepository.findAll().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Double buscarAreaHectares(UUID pastoId) {
        return 0.0;
    }

    @Override
    public Double somarAreaTotal() {
        return springData.somarAreaTotalHectares();
    }


}
