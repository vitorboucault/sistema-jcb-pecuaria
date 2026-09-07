package com.br.infra.persistence.adapter;

import com.br.core.domain.model.Despesa;
import com.br.core.domain.model.Pagina;
import com.br.core.domain.repository.DespesaRepository;
import com.br.infra.persistence.entity.DespesaEntity;
import com.br.infra.persistence.mapper.DespesaMapper;
import com.br.infra.persistence.repository.SpringDataDespesaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class DespesaRepositoryImpl implements DespesaRepository {

    private final SpringDataDespesaRepository springDataRepository;
    private final DespesaMapper mapper;

    public DespesaRepositoryImpl(SpringDataDespesaRepository springDataRepository, DespesaMapper mapper) {
        this.springDataRepository = springDataRepository;
        this.mapper = mapper;
    }

    @Override
    public void salvar(Despesa despesa) {
        DespesaEntity entity = mapper.toEntity(despesa);
        springDataRepository.save(entity);
    }

    @Override
    public void excluirPorId(UUID id) {
        springDataRepository.deleteById(id);
    }

    @Override
    public List<Despesa> buscarPorLote(UUID loteId) {
        return springDataRepository.findByTipoAndCentroCustoId("DESPESA", loteId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Despesa> buscarPorAnimal(UUID animalId) {
        return springDataRepository.findByTipoAndCentroCustoIdAndTipoCentroCusto("DESPESA", animalId, "ANIMAL").stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Despesa> buscarPorPeriodo(LocalDate inicio, LocalDate fim) {
        return springDataRepository.findByTipoAndDataTransacaoBetween("DESPESA", inicio, fim).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public BigDecimal somarDespesasNoPeriodo(LocalDate inicio, LocalDate fim) {
        return springDataRepository.somarDespesasNoPeriodo(inicio, fim);
    }
    @Override
    public Pagina<Despesa> buscarTodosPaginado(int pagina, int tamanho) {
        PageRequest pageRequest = PageRequest.of(pagina, tamanho);
        Page<DespesaEntity> pageResult = springDataRepository.findAll(pageRequest); // Nativo do JpaRepository

        List<Despesa> despesas = pageResult.getContent().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());

        return new Pagina<>(despesas, pageResult.getNumber(), pageResult.getSize(), pageResult.getTotalElements(), pageResult.getTotalPages());
    }

    @Override
    public boolean existePorAnimalId(UUID animalId) {
        return springDataRepository.existsByTipoAndCentroCustoIdAndTipoCentroCusto("DESPESA", animalId, "ANIMAL");
    }

}
