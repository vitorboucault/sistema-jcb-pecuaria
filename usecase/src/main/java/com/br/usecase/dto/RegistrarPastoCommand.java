package com.br.usecase.dto;

public record RegistrarPastoCommand(
        String nome,
        double areaHectares,
        double capacidadeSuporteUa
) { }
