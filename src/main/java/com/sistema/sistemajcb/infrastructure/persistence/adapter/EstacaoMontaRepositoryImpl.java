package com.sistema.sistemajcb.infrastructure.persistence.adapter;

import com.sistema.sistemajcb.domain.model.EstacaoMonta;
import com.sistema.sistemajcb.domain.repository.EstacaoMontaRepository;
import com.sistema.sistemajcb.infrastructure.persistence.mapper.EstacaoMontaMapper;
import com.sistema.sistemajcb.infrastructure.persistence.repository.SpringDataEstacaoMontaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class EstacaoMontaRepositoryImpl implements EstacaoMontaRepository {
    private final SpringDataEstacaoMontaRepository springData;
    private final EstacaoMontaMapper mapper;

    public EstacaoMontaRepositoryImpl(SpringDataEstacaoMontaRepository springData, EstacaoMontaMapper mapper) {
        this.springData = springData;
        this.mapper = mapper;
    }

    @Override
    public void salvar(EstacaoMonta estacaoMonta) {
        springData.save(mapper.toEntity(estacaoMonta));
    }

    @Override
    public Optional<EstacaoMonta> buscarPorId(UUID id) {
        return springData.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<EstacaoMonta> buscarEstacoesAbertas() {
        return springData.findByStatus("ABERTA").stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

}
