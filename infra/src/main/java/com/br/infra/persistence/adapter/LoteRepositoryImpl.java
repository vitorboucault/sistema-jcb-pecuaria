package com.br.infra.persistence.adapter;

import com.br.core.domain.model.Lote;
import com.br.core.domain.model.Pagina;
import com.br.core.domain.repository.LoteRepository;
import com.br.infra.persistence.entity.LoteEntity;
import com.br.infra.persistence.mapper.LoteMapper;
import com.br.infra.persistence.repository.SpringDataLoteRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
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
    public Map<UUID, Lote> buscarPorIds(List<UUID> ids) {
        if (ids == null || ids.isEmpty()) {
            return Map.of();
        }

        return springDataRepository.findAllById(ids).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toMap(Lote::getId, lote -> lote));
    }

    @Override
    public List<Lote> buscarAtivos() {
        return springDataRepository.buscarLotesAtivos().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
    @Override
    public Pagina<Lote> buscarTodosPaginado(int pagina, int tamanho) {
        PageRequest pageRequest = PageRequest.of(pagina, tamanho);
        Page<LoteEntity> pageResult = springDataRepository.findByDataEncerramentoIsNull((pageRequest));
        List<Lote> lotes = pageResult.getContent().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());

        return new Pagina<>(lotes, pageResult.getNumber(), pageResult.getSize(), pageResult.getTotalElements(), pageResult.getTotalPages());
    }
}
