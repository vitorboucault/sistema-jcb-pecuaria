package com.br.infra.persistence.mapper;

import com.br.core.domain.enums.CategoriaDespesa;
import com.br.core.domain.enums.TipoDeCusto;
import com.br.core.domain.enums.SituacaoFinanceira;
import com.br.core.domain.enums.TipoLancamentoFinanceiro;
import com.br.core.domain.model.Despesa;
import com.br.core.domain.model.LancamentoFinanceiro;
import com.br.infra.persistence.entity.DespesaEntity;
import org.springframework.stereotype.Component;

@Component
public class DespesaMapper {
    public DespesaEntity toEntity(Despesa despesa) {
        LancamentoFinanceiro lancamento = new LancamentoFinanceiro(despesa.getId(),
                TipoLancamentoFinanceiro.DESPESA, despesa.getDescricao(), despesa.getValor(),
                despesa.getCategoria(), despesa.getDataOcorrencia(), null,
                SituacaoFinanceira.PENDENTE, null, despesa.getTipoCentroCusto(), despesa.getReferenciaId());
        DespesaEntity entity = new DespesaEntity(
                despesa.getId(),
                "DESPESA",
                despesa.getDataOcorrencia(),
                despesa.getValor(),
                despesa.getCategoria().name(),
                despesa.getReferenciaId(),
                despesa.getTipoCentroCusto().name()
        );
        entity.definirDadosFinanceiros(lancamento.getDescricao(), lancamento.getSituacao().name(), null, null);
        if (despesa.getCategoria() == CategoriaDespesa.COMPRA_ANIMAL
                && despesa.getTipoCentroCusto() == TipoDeCusto.ANIMAL) {
            entity.definirOrigem("COMPRA_ANIMAL", despesa.getReferenciaId());
        }
        return entity;
    }

    public Despesa toDomain(DespesaEntity entity) {
        if (!"DESPESA".equals(entity.getTipo())) {
            throw new IllegalArgumentException("Receita não pode ser convertida para Despesa.");
        }
        return new Despesa(
                entity.getId(),
                entity.getDescricao(),
                entity.getValor(),
                entity.getDataTransacao(),
                CategoriaDespesa.valueOf(entity.getCategoria()),
                TipoDeCusto.valueOf(entity.getTipoCentroCusto()),
                entity.getCentroCustoId()
        );
    }
}
