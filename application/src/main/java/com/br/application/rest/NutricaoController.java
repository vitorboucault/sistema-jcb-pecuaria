package com.br.application.rest;

import com.br.usecase.dto.RegistrarFornecimentoCommand;
import com.br.usecase.nutricao.CalcularConversaoAlimentarUseCase;
import com.br.usecase.nutricao.RegistrarFornecimentoRacaoUseCase;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/api/nutricao")
@Tag(name = "Nutrição e Cocho", description = "Controle de arraçoamento e conversão alimentar")
public class NutricaoController {

    private final RegistrarFornecimentoRacaoUseCase registrarFornecimentoUseCase;
    private final CalcularConversaoAlimentarUseCase calcularConversaoAlimentarUseCase;

    public NutricaoController(
            RegistrarFornecimentoRacaoUseCase registrarFornecimentoUseCase,
            CalcularConversaoAlimentarUseCase calcularConversaoAlimentarUseCase) {
        this.registrarFornecimentoUseCase = registrarFornecimentoUseCase;
        this.calcularConversaoAlimentarUseCase = calcularConversaoAlimentarUseCase;
    }

    @PostMapping("/fornecimento")
    public ResponseEntity<Void> registrarFornecimento(@RequestBody RegistrarFornecimentoCommand command) {
        registrarFornecimentoUseCase.executar(command);
        return ResponseEntity.status(201).build();
    }

    @GetMapping("/lote/{loteId}/conversao-alimentar")
    public ResponseEntity<BigDecimal> obterConversaoAlimentar(
            @PathVariable UUID loteId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim) {
        BigDecimal ca = calcularConversaoAlimentarUseCase.executar(loteId, inicio, fim);
        return ResponseEntity.ok(ca);
    }
}
