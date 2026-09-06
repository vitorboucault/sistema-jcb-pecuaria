package com.br.infra.persistence.adapter;

import com.br.core.domain.model.Pesagem;
import com.br.core.domain.repository.PesagemRepository;
import com.br.infra.persistence.entity.PesagemEntity;
import com.br.infra.persistence.mapper.PesagemMapper;
import com.br.infra.persistence.repository.SpringDataPesagemRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
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

    @Override
    public Double calcularGanhoPesoTotalNoPeriodo(LocalDate inicioSafra, LocalDate fimSafra) {
        return springDataRepository.calcularGanhoPesoTotalNoPeriodo(inicioSafra, fimSafra);
    }

}
