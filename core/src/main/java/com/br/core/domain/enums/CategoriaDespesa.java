package com.br.core.domain.enums;

import com.br.core.domain.enums.interfaces.CategoriaFinanceira;

public enum CategoriaDespesa implements CategoriaFinanceira {
    NUTRICAO,       // Sal mineral, raçao, suplemento
    SANIDADE,       // Vacinas, vermífugos, medicamentos
    PASTAGEM,       // Sementes, adubo, calcário, herbicida
    MAO_DE_OBRA,    // Salários, encargos, diárias
    MANUTENCAO,     // Trator, cercas, bebedouros
    IMPOSTOS,       // Funrural, GTA, ITR
    COMPRA_ANIMAL,
    DIVERSOS,
    ENERGIA,
    INTERNET,
    AGUA,
    COMBUSTIVEL,
    SERVICOS
}
