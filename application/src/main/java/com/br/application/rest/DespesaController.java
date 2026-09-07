package com.br.application.rest;

import com.br.usecase.dto.RegistrarDespesaCommand;
import com.br.usecase.financeiro.RegistrarDespesaUseCase;
import com.br.core.domain.model.Despesa;
import com.br.core.domain.model.Pagina;
import com.br.core.domain.repository.DespesaRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.bind.annotation.*;
import com.br.application.dto.RegistrarDespesaRequest;
import jakarta.validation.Valid;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/despesas")
public class DespesaController {
    private final RegistrarDespesaUseCase registrarDespesaUseCase;
    private final DespesaRepository despesaRepository;

    public DespesaController(RegistrarDespesaUseCase registrarDespesaUseCase, DespesaRepository despesaRepository) {
        this.registrarDespesaUseCase = registrarDespesaUseCase;
        this.despesaRepository = despesaRepository;
    }

    public ResponseEntity<Void> lancarDespesa(
            @Valid @RequestBody RegistrarDespesaRequest request
    ) {
        var command = new RegistrarDespesaCommand(
                request.descricao(),
                request.valor(),
                request.dataOcorrencia(),
                request.categoria(),
                request.tipoDeCusto(),
                request.referenciaId()
        );
        registrarDespesaUseCase.executar(command);

        return ResponseEntity.status(201).build();
    }

    @GetMapping
    public ResponseEntity<Pagina<Despesa>> listarExtrato(@RequestParam(defaultValue = "0") int pagina, @RequestParam(defaultValue = "50") int tamanho) {
        Pagina<Despesa> extrato = despesaRepository.buscarTodosPaginado(pagina, tamanho);
        return ResponseEntity.ok(extrato);
    }

    @DeleteMapping("/{id}/estornar")
    public ResponseEntity<Void> estornarDespesa(@PathVariable UUID id) {
        throw new ResponseStatusException(HttpStatus.NOT_IMPLEMENTED,
                "Estorno de despesa ainda não está disponível.");
    }
}
