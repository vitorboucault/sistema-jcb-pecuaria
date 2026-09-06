package com.br.infra;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {
    public record ErroPadraoDTO(LocalDateTime timestamp, Integer status, String erro, String mensagem) {}

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErroPadraoDTO> tratarRegraNegocioInvalida(IllegalArgumentException ex) {
        var erro = new ErroPadraoDTO(LocalDateTime.now(), HttpStatus.BAD_REQUEST.value(), "Regra de Negócio Inválida", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erro);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErroPadraoDTO> tratarEstadoInvalido(IllegalStateException ex) {
        var erro = new ErroPadraoDTO(LocalDateTime.now(), HttpStatus.CONFLICT.value(), "Estado Inconsistente no Curral", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(erro);
    }
}
