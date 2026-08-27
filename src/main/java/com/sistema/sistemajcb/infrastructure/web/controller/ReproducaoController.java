package com.sistema.sistemajcb.infrastructure.web.controller;

import com.sistema.sistemajcb.application.dto.usecase.CalcularTaxaPrenhezUseCase;
import com.sistema.sistemajcb.application.dto.usecase.RegistrarDiagnosticoUseCase;
import com.sistema.sistemajcb.domain.enums.ResultadoDiagnostico;
import com.sistema.sistemajcb.domain.model.DiagnosticoGestacao;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/api/reproducao")
public class ReproducaoController {
    private final RegistrarDiagnosticoUseCase registrarDiagnosticoUseCase;
    private final CalcularTaxaPrenhezUseCase calcularTaxaPrenhezUseCase;

    public ReproducaoController(RegistrarDiagnosticoUseCase registrarDiagnosticoUseCase, CalcularTaxaPrenhezUseCase calcularTaxaPrenhezUseCase) {
        this.registrarDiagnosticoUseCase = registrarDiagnosticoUseCase;
        this.calcularTaxaPrenhezUseCase = calcularTaxaPrenhezUseCase;
    }

    @PostMapping("/diagnostico")
    public ResponseEntity<DiagnosticoGestacao> registrarToque(
            @RequestParam UUID animalId,
            @RequestParam UUID estacaoMontaId,
            @RequestParam ResultadoDiagnostico resultado,
            @RequestParam LocalDate dataInseminacao) {

        DiagnosticoGestacao diagnostico = registrarDiagnosticoUseCase.executar(
                animalId, estacaoMontaId, LocalDate.now(), resultado, dataInseminacao
        );
        return ResponseEntity.ok(diagnostico);
    }

    @GetMapping("/estacao/{estacaoMontaId}/taxa-prenhez")
    public ResponseEntity<BigDecimal> obterTaxaPrenhez(@PathVariable UUID estacaoMontaId) {
        BigDecimal taxa = calcularTaxaPrenhezUseCase.executar(estacaoMontaId);
        return ResponseEntity.ok(taxa);
    }

}
