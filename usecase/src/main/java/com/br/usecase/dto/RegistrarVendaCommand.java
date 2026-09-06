package com.br.usecase.dto;

import com.br.core.domain.enums.ModalidadeVenda;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record RegistrarVendaCommand(
        UUID animalId,
        LocalDate dataVenda,
        ModalidadeVenda modalidade,
        Double pesoVivoKg,
        Double rendimentoCarcacaPercentual,
        BigDecimal precoAcordado
) {}
