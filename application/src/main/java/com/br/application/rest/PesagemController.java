package com.br.application.rest;

import com.br.application.dto.RegistrarPesagemRequest;
import com.br.usecase.dto.RegistrarPesagemCommand;
import com.br.usecase.dto.RegistrarPesagemResult;
import com.br.usecase.manejo.RegistrarPesagemUseCase;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/pesagens")
public class PesagemController {

    private final RegistrarPesagemUseCase registrarPesagemUseCase;

    public PesagemController(
            RegistrarPesagemUseCase registrarPesagemUseCase
    ) {
        this.registrarPesagemUseCase = registrarPesagemUseCase;
    }

    @PostMapping
    public ResponseEntity<RegistrarPesagemResult> registrar(
            @Valid @RequestBody RegistrarPesagemRequest request
    ) {

        var command = new RegistrarPesagemCommand(
                request.animalId(),
                request.dataPesagem(),
                request.pesoKg(),
                request.jejum()
        );

        RegistrarPesagemResult resultado =
                registrarPesagemUseCase.executar(command);

        return ResponseEntity.ok(resultado);
    }
}