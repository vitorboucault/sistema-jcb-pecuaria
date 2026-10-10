package com.br.infra.persistence.adapter;

import com.br.core.domain.model.AquisicaoAnimal;
import com.br.core.domain.repository.AquisicaoAnimalRepository;
import com.br.infra.persistence.entity.AquisicaoAnimalEntity;
import com.br.infra.persistence.mapper.AquisicaoAnimalMapper;
import com.br.infra.persistence.repository.SpringDataAquisicaoAnimalRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class AquisicaoAnimalRepositoryImpl implements AquisicaoAnimalRepository {

    private final SpringDataAquisicaoAnimalRepository springDataRepository;
    private final AquisicaoAnimalMapper mapper;

    public AquisicaoAnimalRepositoryImpl(
            SpringDataAquisicaoAnimalRepository springDataRepository,
            AquisicaoAnimalMapper mapper
    ) {
        this.springDataRepository = springDataRepository;
        this.mapper = mapper;
    }

    @Override
    public void salvar(AquisicaoAnimal aquisicao) {
        AquisicaoAnimalEntity entity = mapper.toEntity(aquisicao);
        springDataRepository.save(entity);
    }

    @Override
    public Optional<AquisicaoAnimal> buscarPorAnimalId(UUID animalId) {
        return springDataRepository.findByAnimalId(animalId)
                .map(mapper::toDomain);
    }
}
