package com.sistema.sistemajcb.infrastructure.persistence.mapper;

import com.sistema.sistemajcb.domain.enums.CategoriaDespesa;
import com.sistema.sistemajcb.domain.enums.TipoDeCusto;
import com.sistema.sistemajcb.domain.model.Despesa;
import com.sistema.sistemajcb.infrastructure.persistence.entity.DespesaEntity;
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
                "Despesa importada do banco", // A tabela atual não possui coluna de descrição detalhada
                entity.getValor(),
                entity.getDataTransacao(),
                CategoriaDespesa.valueOf(entity.getCategoria()),
                TipoDeCusto.valueOf(entity.getTipoCentroCusto()),
                entity.getCentroCustoId()
        );
    }
}
