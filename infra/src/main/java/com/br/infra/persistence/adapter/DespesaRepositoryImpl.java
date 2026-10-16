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
import org.springframework.transaction.annotation.Transactional;

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
    @Transactional
    public void salvar(Despesa despesa) {
        DespesaEntity entity = springDataRepository.findById(despesa.getId()).map(existente -> {
            if (!"DESPESA".equals(existente.getTipo())) {
                throw new IllegalArgumentException("Receita não pode ser salva pela visão Despesa.");
            }
            existente.atualizarDadosDespesa(despesa.getDataOcorrencia(), despesa.getValor(),
                    despesa.getCategoria().name(), despesa.getReferenciaId(), despesa.getTipoCentroCusto().name());
            return existente;
        }).orElseGet(() -> mapper.toEntity(despesa));
        springDataRepository.save(entity);
    }

    @Override
    public void excluirPorId(UUID id) {
        springDataRepository.findById(id).filter(entity -> "DESPESA".equals(entity.getTipo()))
                .ifPresent(springDataRepository::delete);
    }

    @Override
    public List<Despesa> buscarPorLote(UUID loteId) {
        return springDataRepository.findByTipoAndCentroCustoIdAndTipoCentroCusto("DESPESA", loteId, "LOTE").stream()
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
        Page<DespesaEntity> pageResult = springDataRepository.findByTipo("DESPESA", pageRequest);

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
