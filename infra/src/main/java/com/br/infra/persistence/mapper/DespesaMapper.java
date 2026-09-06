package com.br.infra.persistence.mapper;

import com.br.core.domain.enums.CategoriaDespesa;
import com.br.core.domain.enums.TipoDeCusto;
import com.br.core.domain.model.Despesa;
import com.br.infra.persistence.entity.DespesaEntity;
import org.springframework.stereotype.Component;

@Component
public class DespesaMapper {
    public DespesaEntity toEntity(Despesa despesa) {
        return new DespesaEntity(
                despesa.getId(),
                "DESPESA",
                despesa.getDataOcorrencia(),
                despesa.getValor(),
                despesa.getCategoria().name(),
                despesa.getReferenciaId(),
                despesa.getTipoCentroCusto().name()
        );
    }

    public Despesa toDomain(DespesaEntity entity) {
        return new Despesa(
                entity.getId(),
                "Despesa importada do banco", // A tabela atual nao possui coluna de descriçao detalhada
                entity.getValor(),
                entity.getDataTransacao(),
                CategoriaDespesa.valueOf(entity.getCategoria()),
                TipoDeCusto.valueOf(entity.getTipoCentroCusto()),
                entity.getCentroCustoId()
        );
    }
}
