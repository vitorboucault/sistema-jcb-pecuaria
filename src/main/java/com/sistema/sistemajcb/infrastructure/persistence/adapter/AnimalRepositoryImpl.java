package com.sistema.sistemajcb.infrastructure.persistence.adapter;

import com.sistema.sistemajcb.domain.Animal;
import com.sistema.sistemajcb.infrastructure.persistence.entity.AnimalEntity;
import com.sistema.sistemajcb.infrastructure.persistence.mapper.AnimalMapper;
import com.sistema.sistemajcb.domain.repository.AnimalRepository;
import com.sistema.sistemajcb.infrastructure.persistence.repository.SpringDataAnimalRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AnimalRepositoryImpl implements AnimalRepository {

    private final SpringDataAnimalRepository springDataRepository;
    private final AnimalMapper mapper;

    public AnimalRepositoryImpl(SpringDataAnimalRepository springDataRepository, AnimalMapper mapper) {
        this.springDataRepository = springDataRepository;
        this.mapper = mapper;
    }

    @Override
    public void salvar(Animal animal) {
        AnimalEntity entity = mapper.toEntity(animal);
        springDataRepository.save(entity);
    }

    @Override
    public Optional<Animal> buscarPorId(UUID id) {
        return springDataRepository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public Optional<Animal> buscarPorBrinco(String brinco) {
        return springDataRepository.findByBrincoRgd(brinco)
                .map(mapper::toDomain);
    }

    @Override
    public List<Animal> buscarAnimaisElegiveisParaEvolucao() {
        return springDataRepository.buscarAnimaisElegiveisParaEvolucao()
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Animal> buscarPorLote(UUID loteId) {
        return springDataRepository.findByLoteAtual(loteId)
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public long contarAnimaisAtivos() {
        return springDataRepository.countByStatus("ATIVO");
    }
}
