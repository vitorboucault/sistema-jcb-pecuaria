package com.br.application.rest;

import com.br.usecase.dto.RegistrarPastoCommand;
import com.br.usecase.manejo.RegistrarPastoUseCase;
import com.br.core.domain.model.Pagina;
import com.br.core.domain.model.Pasto;
import com.br.core.domain.repository.PastoRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/pastos")
public class PastoController {
    private final PastoRepository pastoRepository;
    private final RegistrarPastoUseCase registrarPastoUseCase;

    public PastoController(PastoRepository pastoRepository,  RegistrarPastoUseCase registrarPastoUseCase) {
        this.pastoRepository = pastoRepository;
        this.registrarPastoUseCase = registrarPastoUseCase;
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
        // Caso de Uso para fechar o pasto para adubaçao/descanso
        // atualizarPastoUseCase.iniciarManutencao(id);
        return ResponseEntity.noContent().build();
    }
}
