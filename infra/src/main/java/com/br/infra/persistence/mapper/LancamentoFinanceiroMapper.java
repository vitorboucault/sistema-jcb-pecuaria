package com.br.infra.persistence.mapper;

import com.br.core.domain.enums.CategoriaDespesa;
import com.br.core.domain.enums.CategoriaReceita;
import com.br.core.domain.enums.SituacaoFinanceira;
import com.br.core.domain.enums.TipoDeCusto;
import com.br.core.domain.enums.TipoLancamentoFinanceiro;
import com.br.core.domain.enums.interfaces.CategoriaFinanceira;
import com.br.core.domain.model.LancamentoFinanceiro;
import com.br.infra.persistence.entity.DespesaEntity;
import org.springframework.stereotype.Component;

@Component
public class LancamentoFinanceiroMapper {
    public DespesaEntity toEntity(LancamentoFinanceiro lancamento) {
        String categoria = lancamento.getCategoria() instanceof CategoriaDespesa despesa
                ? despesa.name() : ((CategoriaReceita) lancamento.getCategoria()).name();
        DespesaEntity entity = new DespesaEntity(lancamento.getId(), lancamento.getTipo().name(),
                lancamento.getDataReferencia(), lancamento.getValor(), categoria,
                lancamento.getReferenciaId(), lancamento.getTipoDeCusto().name());
        entity.definirDadosFinanceiros(lancamento.getDescricao(), lancamento.getSituacao().name(),
                lancamento.getDataVencimento(), lancamento.getDataEfetivacao());
        return entity;
    }

    public LancamentoFinanceiro toDomain(DespesaEntity entity) {
        TipoLancamentoFinanceiro tipo = TipoLancamentoFinanceiro.valueOf(entity.getTipo());
        CategoriaFinanceira categoria = tipo == TipoLancamentoFinanceiro.DESPESA
                ? CategoriaDespesa.valueOf(entity.getCategoria()) : CategoriaReceita.valueOf(entity.getCategoria());
        return new LancamentoFinanceiro(entity.getId(), tipo, entity.getDescricao(), entity.getValor(),
                categoria, entity.getDataTransacao(), entity.getDataVencimento(),
                SituacaoFinanceira.valueOf(entity.getSituacao()), entity.getDataEfetivacao(),
                TipoDeCusto.valueOf(entity.getTipoCentroCusto()), entity.getCentroCustoId());
    }

}
