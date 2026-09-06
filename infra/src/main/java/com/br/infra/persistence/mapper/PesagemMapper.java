package com.br.infra.persistence.mapper;

import com.br.core.domain.model.Pesagem;
import com.br.infra.persistence.entity.PesagemEntity;
import org.springframework.stereotype.Component;

@Component
public class PesagemMapper {
    public PesagemEntity toEntity(Pesagem pesagem) {
        return new PesagemEntity(pesagem.getId(), pesagem.getAnimalId(), pesagem.getDataPesagem(), pesagem.getPeso());
    }

    public Pesagem toDomain(PesagemEntity entity) {
        // Passando 'false' temporariamente para o jejum, pois ainda nao está na tabela
        return new Pesagem(entity.getId(), entity.getAnimalId(), entity.getDataPesagem(), entity.getPeso(), false);
    }
}
