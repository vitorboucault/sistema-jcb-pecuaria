package com.br.application.rest;

import com.br.usecase.dto.RegistrarPastoCommand;
import com.br.usecase.manejo.RegistrarPastoUseCase;
import com.br.usecase.manejo.IniciarManutencaoPastoUseCase;
import com.br.core.domain.model.Pagina;
import com.br.core.domain.model.Pasto;
import com.br.core.domain.repository.PastoRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/pastos")
public class PastoController {
    private final PastoRepository pastoRepository;
    private final RegistrarPastoUseCase registrarPastoUseCase;
    private final IniciarManutencaoPastoUseCase iniciarManutencaoPastoUseCase;

    public PastoController(PastoRepository pastoRepository, RegistrarPastoUseCase registrarPastoUseCase,
                           IniciarManutencaoPastoUseCase iniciarManutencaoPastoUseCase) {
        this.pastoRepository = pastoRepository;
        this.registrarPastoUseCase = registrarPastoUseCase;
        this.iniciarManutencaoPastoUseCase = iniciarManutencaoPastoUseCase;
    }

    @PostMapping
    public ResponseEntity<UUID> cadastrar(@RequestBody RegistrarPastoCommand command) {
        UUID novoPastoId = registrarPastoUseCase.executar(command);
        return ResponseEntity.status(201).body(novoPastoId);
    }

    @GetMapping
    public ResponseEntity<Pagina<Pasto>> listar(@RequestParam(defaultValue = "0") int pagina,
                                                @RequestParam(defaultValue = "10") int tamanho) {
        Pagina<Pasto> resultado = pastoRepository.buscarTodosPaginado(pagina, tamanho);
        return ResponseEntity.ok(resultado);
    }
    @GetMapping("/{id}")
    public ResponseEntity<Pasto> buscarPorId(@PathVariable UUID id) {
        return pastoRepository.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/manutencao")
    public ResponseEntity<Void> iniciarReforma(@PathVariable UUID id) {
        iniciarManutencaoPastoUseCase.executar(id);
        return ResponseEntity.noContent().build();
    }
}
