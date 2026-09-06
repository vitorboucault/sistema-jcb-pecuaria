package com.br.application.rest;

import com.br.usecase.dto.RegistrarPesagemCommand;
import com.br.usecase.dto.RegistrarPesagemResult;
import com.br.usecase.manejo.RegistrarPesagemUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/pesagens")
public class PesagemController {
    private final RegistrarPesagemUseCase registrarPesagemUseCase;

    public PesagemController(RegistrarPesagemUseCase registrarPesagemUseCase) {
        this.registrarPesagemUseCase = registrarPesagemUseCase;
    }

    @PostMapping
    public ResponseEntity<RegistrarPesagemResult> registrar(@RequestBody RegistrarPesagemCommand command) {
        RegistrarPesagemResult resultado = registrarPesagemUseCase.executar(command);
        return ResponseEntity.ok(resultado);
    }
}
