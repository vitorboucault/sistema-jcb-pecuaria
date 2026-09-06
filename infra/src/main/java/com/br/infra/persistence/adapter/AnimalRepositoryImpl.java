package com.br.infra.persistence.adapter;

import com.br.core.domain.model.Animal;
import com.br.core.domain.model.Pagina;
import com.br.infra.persistence.entity.AnimalEntity;
import com.br.infra.persistence.mapper.AnimalMapper;
import com.br.core.domain.repository.AnimalRepository;
import com.br.infra.persistence.repository.SpringDataAnimalRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
    public Animal salvar(Animal animal) {
        AnimalEntity entity = mapper.toEntity(animal);
        springDataRepository.save(entity);
        return animal;
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

    @Override
    public Pagina<Animal> buscarTodosPaginado(int pagina, int tamanho) {
        PageRequest pageRequest = PageRequest.of(pagina, tamanho);
        Page<AnimalEntity> pageResult = springDataRepository.findByStatus("ATIVO", pageRequest);

        List<Animal> animais = pageResult.getContent().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
        return new Pagina<>(animais, pageResult.getNumber(), pageResult.getSize(),
                pageResult.getTotalElements(), pageResult.getTotalPages());
    }
}
