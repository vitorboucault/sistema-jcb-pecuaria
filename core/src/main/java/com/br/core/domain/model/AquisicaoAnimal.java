package com.br.core.domain.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public class AquisicaoAnimal {

    private final UUID id;
    private final UUID animalId;
    private final LocalDate dataAquisicao;
    private final BigDecimal valorAquisicao;

    public AquisicaoAnimal(
            UUID id,
            UUID animalId,
            LocalDate dataAquisicao,
            BigDecimal valorAquisicao
    ) {
        this(id, animalId, dataAquisicao, valorAquisicao, null);
    }

    public AquisicaoAnimal(
            UUID id,
            UUID animalId,
            LocalDate dataAquisicao,
            BigDecimal valorAquisicao,
            LocalDate dataNascimento
    ) {
        if (id == null) {
            throw new IllegalArgumentException("O ID da aquisição é obrigatório.");
        }
        if (animalId == null) {
            throw new IllegalArgumentException("O animal da aquisição é obrigatório.");
        }
        if (dataAquisicao == null && valorAquisicao == null) {
            throw new IllegalArgumentException("A aquisição deve possuir data ou valor conhecido.");
        }
        if (valorAquisicao != null && valorAquisicao.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("O valor da aquisição deve ser maior que zero.");
        }
        if (dataAquisicao != null && dataAquisicao.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("A data da aquisição não pode ser futura.");
        }
        if (dataAquisicao != null && dataNascimento != null && dataAquisicao.isBefore(dataNascimento)) {
            throw new IllegalArgumentException("A data da aquisição não pode ser anterior à data de nascimento.");
        }

        this.id = id;
        this.animalId = animalId;
        this.dataAquisicao = dataAquisicao;
        this.valorAquisicao = valorAquisicao;
    }

    public UUID getId() {
        return id;
    }

    public UUID getAnimalId() {
        return animalId;
    }

    public LocalDate getDataAquisicao() {
        return dataAquisicao;
    }

    public BigDecimal getValorAquisicao() {
        return valorAquisicao;
    }
}
