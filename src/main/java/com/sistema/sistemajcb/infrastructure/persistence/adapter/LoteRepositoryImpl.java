package com.sistema.sistemajcb.infrastructure.persistence.adapter;

import com.sistema.sistemajcb.domain.model.Lote;
import com.sistema.sistemajcb.domain.repository.LoteRepository;
import com.sistema.sistemajcb.infrastructure.persistence.entity.LoteEntity;
import com.sistema.sistemajcb.infrastructure.persistence.mapper.LoteMapper;
import com.sistema.sistemajcb.infrastructure.persistence.repository.SpringDataLoteRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class LoteRepositoryImpl implements LoteRepository {

    private final SpringDataLoteRepository springDataRepository;
    private final LoteMapper mapper;

    public LoteRepositoryImpl(SpringDataLoteRepository springDataRepository, LoteMapper mapper) {
        this.springDataRepository = springDataRepository;
        this.mapper = mapper;
    }

    @Override
    public void salvar(Lote lote) {
        LoteEntity entity = mapper.toEntity(lote);
        springDataRepository.save(entity);
    }

    @Override
    public Optional<Lote> buscarPorId(UUID id) {
        return springDataRepository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public List<Lote> buscarAtivos() {
        return springDataRepository.buscarLotesAtivos().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
}
