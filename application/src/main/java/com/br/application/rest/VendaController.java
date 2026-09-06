package com.br.application.rest;

import com.br.usecase.dto.RegistrarVendaCommand;
import com.br.usecase.manejo.RegistrarVendaAnimalUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/vendas")
public class VendaController {
    private final RegistrarVendaAnimalUseCase registrarVendaAnimalUseCase;

    public VendaController(RegistrarVendaAnimalUseCase registrarVendaAnimalUseCase) {
        this.registrarVendaAnimalUseCase = registrarVendaAnimalUseCase;
    }
    @PostMapping
    public ResponseEntity<UUID> registrarVenda(@RequestBody RegistrarVendaCommand command) {
        UUID vendaId = registrarVendaAnimalUseCase.executar(command);
        return ResponseEntity.status(201).body(vendaId);
    }
}
