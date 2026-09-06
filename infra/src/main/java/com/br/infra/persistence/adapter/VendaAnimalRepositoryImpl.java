package com.br.infra.persistence.adapter;

import com.br.core.domain.model.VendaAnimal;
import com.br.core.domain.repository.VendaAnimalRepository;
import com.br.infra.persistence.mapper.VendaAnimalMapper;
import com.br.infra.persistence.repository.SpringDataVendaAnimalRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
public class VendaAnimalRepositoryImpl implements VendaAnimalRepository {
    private final SpringDataVendaAnimalRepository springData;
    private final VendaAnimalMapper mapper; // (Crie o Mapper no padrao que já usamos)

    public VendaAnimalRepositoryImpl(SpringDataVendaAnimalRepository springData, VendaAnimalMapper mapper) {
        this.springData = springData;
        this.mapper = mapper;
    }

    @Override
    public void salvar(VendaAnimal venda) {
        springData.save(mapper.toEntity(venda));
    }

    @Override
    public BigDecimal somarReceitasNoPeriodo(LocalDate inicio, LocalDate fim) {
        // O Domínio resolve a regra da modalidade de venda, o adaptador só soma
        return springData.findByDataVendaBetween(inicio, fim).stream()
                .map(mapper::toDomain)
                .map(VendaAnimal::calcularReceitaTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
