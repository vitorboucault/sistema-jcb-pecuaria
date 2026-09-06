package com.br.infra.persistence.adapter;

import com.br.core.domain.model.Pagina;
import com.br.core.domain.model.Pasto;
import com.br.core.domain.repository.LoteRepository;
import com.br.core.domain.repository.PastoRepository;
import com.br.infra.persistence.entity.PastoEntity;
import com.br.infra.persistence.mapper.PastoMapper;
import com.br.infra.persistence.repository.SpringDataLoteRepository;
import com.br.infra.persistence.repository.SpringDataPastoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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

        return springDataRepository.findById(pastoId)
                .map(PastoEntity::getAreaHectares)
                .orElse(0.0);
    }

    @Override
    public Double somarAreaTotal() {
        return springDataRepository.somarAreaTotalHectares();
    }
    @Override
    public Pagina<Pasto> buscarTodosPaginado(int pagina, int tamanho) {
        PageRequest pageRequest = PageRequest.of(pagina, tamanho);
        Page<PastoEntity> pageResult = springDataRepository.findAll(pageRequest); // Nativo do JpaRepository

        List<Pasto> pastos = pageResult.getContent().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
        return new Pagina<>(pastos, pageResult.getNumber(), pageResult.getSize(), pageResult.getTotalElements(), pageResult.getTotalPages());
    }


}
