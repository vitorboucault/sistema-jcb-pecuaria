package com.br.core.domain.model;

import java.util.List;

public record Pagina<T>(
        List<T> conteudo,
        int numeroPagina,
        int tamanhoPagina,
        long totalElementos,
        int totalPaginas
) {}
