package com.br.infra.persistence.adapter;

import com.br.core.domain.model.LancamentoFinanceiro;
import com.br.core.domain.repository.LancamentoFinanceiroRepository;
import com.br.infra.persistence.mapper.LancamentoFinanceiroMapper;
import com.br.infra.persistence.repository.SpringDataDespesaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
public class LancamentoFinanceiroRepositoryImpl implements LancamentoFinanceiroRepository {
    private final SpringDataDespesaRepository repository;
    private final LancamentoFinanceiroMapper mapper;

    public LancamentoFinanceiroRepositoryImpl(SpringDataDespesaRepository repository, LancamentoFinanceiroMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    @Transactional
    public void salvar(LancamentoFinanceiro lancamento) {
        var entity = mapper.toEntity(lancamento);
        repository.findById(lancamento.getId()).ifPresent(existente -> {
            entity.definirOrigem(existente.getOrigemTipo(), existente.getOrigemId());
        });
        repository.save(entity);
    }

    @Override
    public Optional<LancamentoFinanceiro> buscarPorId(UUID id) {
        return repository.findById(id).map(mapper::toDomain);
    }

}
