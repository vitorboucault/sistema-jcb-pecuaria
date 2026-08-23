package com.sistema.sistemajcb.infrastructure.web.controller;

import com.sistema.sistemajcb.application.dto.usecase.CalcularCustoArrobaUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("/api/lotes")
public class LoteController {
    private final CalcularCustoArrobaUseCase calcularCustoArrobaUseCase;

    public LoteController(CalcularCustoArrobaUseCase calcularCustoArrobaUseCase) {
        this.calcularCustoArrobaUseCase = calcularCustoArrobaUseCase;
    }

    @GetMapping("/{loteId}/custo-arroba")
    public ResponseEntity<BigDecimal> obterCustoArrobaProduzida(@PathVariable UUID loteId) {
        BigDecimal custo = calcularCustoArrobaUseCase.executar(loteId);
        return ResponseEntity.ok(custo);
    }

}
