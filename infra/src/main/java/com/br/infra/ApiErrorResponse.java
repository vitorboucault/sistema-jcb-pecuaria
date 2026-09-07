package com.br.infra;

import java.time.LocalDateTime;
import java.util.Map;

public record ApiErrorResponse(
        LocalDateTime timestamp,
        int status,
        String erro,
        String mensagem,
        Map<String, String> campos
) {

    public static ApiErrorResponse simples(
            int status,
            String erro,
            String mensagem
    ) {
        return new ApiErrorResponse(
                LocalDateTime.now(),
                status,
                erro,
                mensagem,
                Map.of()
        );
    }
}