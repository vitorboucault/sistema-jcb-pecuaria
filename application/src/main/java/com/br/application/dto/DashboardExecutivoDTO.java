package com.br.application.dto;

public record DashboardExecutivoDTO(
        Double margemBrutaHectare,
        Double pontoEquilibrioArrobas,
        Double desembolsoCabecaMes,
        Double custoArrobaProduzida,
        Double ganhoMedioDiarioGlobal,
        Double conversaoAlimentarMedia,
        Double taxaPrenhez,
        Double taxaDesmame,
        Double taxaLotacao
) { }
