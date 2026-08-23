package com.sistema.sistemajcb.infrastructure.persistence.adapter;

import com.sistema.sistemajcb.domain.model.Pesagem;
import com.sistema.sistemajcb.domain.repository.PesagemRepository;
import com.sistema.sistemajcb.infrastructure.persistence.entity.PesagemEntity;
import com.sistema.sistemajcb.infrastructure.persistence.mapper.PesagemMapper;
import com.sistema.sistemajcb.infrastructure.persistence.repository.SpringDataPesagemRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PesagemRepositoryImpl implements PesagemRepository {


    private final SpringDataPesagemRepository springDataRepository;
    private final PesagemMapper mapper;


    public PesagemRepositoryImpl(SpringDataPesagemRepository springDataRepository, PesagemMapper mapper) {
        this.springDataRepository = springDataRepository;
        this.mapper = mapper;
    }
    @Override
    public void salvar(Pesagem pesagem) {
        PesagemEntity entity = mapper.toEntity(pesagem);
        springDataRepository.save(entity);
    }
    @Override
    public List<Pesagem> buscarPesagemPorId(UUID id) {
        return springDataRepository.findById(id)
                .map(mapper::toDomain)
                .map(List::of)
                .orElseGet(List::of);
    }

    @Override
    public Optional<Pesagem> buscarUltimaPesagemDoAnimal(UUID animalId) {
        return springDataRepository.findFirstByAnimalIdOrderByDataPesagemDesc(animalId)
                .map(mapper::toDomain);
    }

    @Override
    public List<Pesagem> buscarHistoricoPorAnimal(UUID id) {
        return springDataRepository.findByAnimalIdOrderByDataPesagemAsc(id).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

}
