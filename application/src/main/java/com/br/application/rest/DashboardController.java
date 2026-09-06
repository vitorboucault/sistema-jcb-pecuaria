package com.br.application.rest;

import com.br.usecase.sistema.DashboardExecutivoDTO;
import com.br.usecase.sistema.GerarDashboardExecutivoUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/dashboard")
@Tag(name = "Dashboard Executivo", description = "Métricas consolidadas de desempenho zootécnico e financeiro")
public class DashboardController {

    private final GerarDashboardExecutivoUseCase gerarDashboardUseCase;

    public DashboardController(GerarDashboardExecutivoUseCase gerarDashboardUseCase) {
        this.gerarDashboardUseCase = gerarDashboardUseCase;
    }

    @GetMapping("/executivo")
    @Operation(summary = "Obter visao consolidada da safra",
            description = "Retorna os 7 KPIs principais cruzando dados de reproduçao, pastagem e financeiro.")
    public ResponseEntity<DashboardExecutivoDTO> obterDashboardExecutivo(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicioSafra,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fimSafra,
            @RequestParam BigDecimal precoArrobaHoje,
            @RequestParam(required = false) UUID estacaoMontaId,
            @RequestParam(required = false) UUID pastoReferenciaId,
            @RequestParam(required = false) UUID loteReferenciaId) {

        DashboardExecutivoDTO dashboard = gerarDashboardUseCase.executar(
                inicioSafra,
                fimSafra,
                precoArrobaHoje,
                estacaoMontaId,
                pastoReferenciaId,
                loteReferenciaId
        );
        return ResponseEntity.ok(dashboard);
    }
}