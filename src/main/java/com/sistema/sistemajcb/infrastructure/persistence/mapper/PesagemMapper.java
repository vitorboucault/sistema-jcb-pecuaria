package com.sistema.sistemajcb.infrastructure.persistence.mapper;

import com.sistema.sistemajcb.domain.model.Pesagem;
import com.sistema.sistemajcb.infrastructure.persistence.entity.PesagemEntity;
import org.springframework.stereotype.Component;

@Component
public class PesagemMapper {
    public PesagemEntity toEntity(Pesagem pesagem) {
        return new PesagemEntity(pesagem.getId(), pesagem.getAnimalId(), pesagem.getDataPesagem(), pesagem.getPesoKg());
    }

    public Pesagem toDomain(PesagemEntity entity) {
        // Passando 'false' temporariamente para o jejum, pois ainda não está na tabela
        return new Pesagem(entity.getId(), entity.getAnimalId(), entity.getDataPesagem(), entity.getPesoKg(), false);
    }
}
