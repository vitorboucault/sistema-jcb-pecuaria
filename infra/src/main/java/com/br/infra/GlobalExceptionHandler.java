package com.br.infra;

import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log =
            LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> tratarValidacao(
            MethodArgumentNotValidException ex
    ) {

        Map<String, String> campos = new LinkedHashMap<>();

        ex.getBindingResult()
                .getFieldErrors()
                .forEach(error ->
                        campos.put(
                                error.getField(),
                                error.getDefaultMessage()
                        )
                );

        var erro = new ApiErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Dados inválidos",
                "Um ou mais campos possuem valores inválidos.",
                campos
        );

        return ResponseEntity
                .badRequest()
                .body(erro);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiErrorResponse> tratarConstraintViolation(
            ConstraintViolationException ex
    ) {

        Map<String, String> campos = new LinkedHashMap<>();

        ex.getConstraintViolations()
                .forEach(violacao ->
                        campos.put(
                                violacao.getPropertyPath().toString(),
                                violacao.getMessage()
                        )
                );

        var erro = new ApiErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Dados inválidos",
                "Um ou mais parâmetros possuem valores inválidos.",
                campos
        );

        return ResponseEntity
                .badRequest()
                .body(erro);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiErrorResponse> tratarJsonInvalido(
            HttpMessageNotReadableException ex
    ) {

        var erro = ApiErrorResponse.simples(
                HttpStatus.BAD_REQUEST.value(),
                "Requisição inválida",
                "O corpo da requisição está ausente ou possui formato inválido."
        );

        return ResponseEntity
                .badRequest()
                .body(erro);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiErrorResponse> tratarTipoParametroInvalido(
            MethodArgumentTypeMismatchException ex
    ) {

        String mensagem = String.format(
                "O valor informado para '%s' é inválido.",
                ex.getName()
        );

        var erro = ApiErrorResponse.simples(
                HttpStatus.BAD_REQUEST.value(),
                "Parâmetro inválido",
                mensagem
        );

        return ResponseEntity
                .badRequest()
                .body(erro);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiErrorResponse> tratarParametroObrigatorioAusente(
            MissingServletRequestParameterException ex
    ) {

        String mensagem = String.format(
                "O parâmetro '%s' é obrigatório.",
                ex.getParameterName()
        );

        var erro = ApiErrorResponse.simples(
                HttpStatus.BAD_REQUEST.value(),
                "Parâmetro obrigatório ausente",
                mensagem
        );

        return ResponseEntity
                .badRequest()
                .body(erro);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErrorResponse> tratarRegraNegocioInvalida(
            IllegalArgumentException ex
    ) {

        var erro = ApiErrorResponse.simples(
                HttpStatus.BAD_REQUEST.value(),
                "Regra de negócio inválida",
                ex.getMessage()
        );

        return ResponseEntity
                .badRequest()
                .body(erro);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiErrorResponse> tratarEstadoInvalido(
            IllegalStateException ex
    ) {

        var erro = ApiErrorResponse.simples(
                HttpStatus.CONFLICT.value(),
                "Estado inconsistente",
                ex.getMessage()
        );

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(erro);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ApiErrorResponse> tratarResponseStatus(
            ResponseStatusException ex
    ) {

        int status = ex.getStatusCode().value();

        String mensagem = ex.getReason() != null
                ? ex.getReason()
                : "A requisição não pôde ser processada.";

        var erro = ApiErrorResponse.simples(
                status,
                "Erro na requisição",
                mensagem
        );

        return ResponseEntity
                .status(ex.getStatusCode())
                .body(erro);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> tratarErroInterno(
            Exception ex
    ) {

        log.error("Erro interno não tratado pela aplicação", ex);

        var erro = ApiErrorResponse.simples(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Erro interno",
                "Ocorreu um erro interno no servidor."
        );

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(erro);
    }
}