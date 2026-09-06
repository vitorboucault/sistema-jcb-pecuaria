package com.br.application.rest;

import com.br.usecase.dto.AbrirLoteCommand;
import com.br.usecase.manejo.AbrirLoteUseCase;
import com.br.usecase.financeiro.CalcularCustoArrobaUseCase;
import com.br.core.domain.model.Lote;
import com.br.core.domain.model.Pagina;
import com.br.core.domain.repository.LoteRepository;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/lotes")
@Tag(name = "Controle de Lote", description = "Métricas de arroba por lote")
public class LoteController {
    private final CalcularCustoArrobaUseCase calcularCustoArrobaUseCase;
    private final LoteRepository loteRepository;
    private final AbrirLoteUseCase abrirLoteUseCase;

    public LoteController(CalcularCustoArrobaUseCase calcularCustoArrobaUseCase,  LoteRepository loteRepository,
                          AbrirLoteUseCase abrirLoteUseCase) {
        this.calcularCustoArrobaUseCase = calcularCustoArrobaUseCase;
        this.loteRepository = loteRepository;
        this.abrirLoteUseCase = abrirLoteUseCase;
    }
    public record LoteDTO(
            Long id,
            String nome,
            String descricao,
            Integer quantidadeAnimais,
            Double pesoMedio,
            String categoriaPredominante
    ) {}

    @GetMapping("/{loteId}/custo-arroba")
    public ResponseEntity<BigDecimal> obterCustoArrobaProduzida(@PathVariable UUID loteId) {
        BigDecimal custo = calcularCustoArrobaUseCase.executar(loteId);
        return ResponseEntity.ok(custo);
    }

    @PostMapping
    public ResponseEntity<UUID> abrirLote(@RequestBody AbrirLoteCommand command) {
        UUID novoLoteId = abrirLoteUseCase.executar(command);
        return ResponseEntity.status(201).body(novoLoteId);
    }

    @GetMapping
    public ResponseEntity<Pagina<Lote>> listarAtivos(@RequestParam(defaultValue = "0") int pagina, @RequestParam(defaultValue = "20") int tamanho) {
        Pagina<Lote> resultado = loteRepository.buscarTodosPaginado(pagina, tamanho);
        return ResponseEntity.ok(resultado);
    }

    @PutMapping("/{id}/encerrar")
    public ResponseEntity<Void> encerrarLote(@PathVariable UUID id) {
        return ResponseEntity.noContent().build();
    }



}
