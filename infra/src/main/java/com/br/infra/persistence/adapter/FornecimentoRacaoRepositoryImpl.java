package com.br.infra.persistence.adapter;

import com.br.infra.persistence.entity.FornecimentoRacaoEntity;
import com.br.infra.persistence.repository.SpringDataFornecimentoRacaoRepository;
import com.br.usecase.port.FornecimentoRacaoRepositoryPort;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.UUID;

@Component
public class FornecimentoRacaoRepositoryImpl implements FornecimentoRacaoRepositoryPort {

    private final SpringDataFornecimentoRacaoRepository springDataRepository;

    public FornecimentoRacaoRepositoryImpl(SpringDataFornecimentoRacaoRepository springDataRepository) {
        this.springDataRepository = springDataRepository;
    }

    @Override
    public void salvar(UUID loteId, LocalDate dataFornecimento, Double quantidadeKg, Double teorMateriaSeca) {
        FornecimentoRacaoEntity entity = new FornecimentoRacaoEntity(
                UUID.randomUUID(), loteId, dataFornecimento, quantidadeKg, teorMateriaSeca
        );
        springDataRepository.save(entity);
    }

    @Override
    public Double somarConsumoMateriaSecaPorLoteNoPeriodo(UUID loteId, LocalDate inicio, LocalDate fim) {
        return springDataRepository.somarConsumoMateriaSecaPorLoteNoPeriodo(loteId, inicio, fim);
    }
}
