package com.br.application.rest;

import com.br.usecase.dto.RegistrarVendaCommand;
import com.br.usecase.manejo.RegistrarVendaAnimalUseCase;
import com.br.usecase.manejo.ReverterVendaAnimalUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/vendas")
public class VendaController {
    private final RegistrarVendaAnimalUseCase registrarVendaAnimalUseCase;
    private final ReverterVendaAnimalUseCase reverterVendaAnimalUseCase;

    public VendaController(RegistrarVendaAnimalUseCase registrarVendaAnimalUseCase,
                           ReverterVendaAnimalUseCase reverterVendaAnimalUseCase) {
        this.registrarVendaAnimalUseCase = registrarVendaAnimalUseCase;
        this.reverterVendaAnimalUseCase = reverterVendaAnimalUseCase;
    }
    @PostMapping
    public ResponseEntity<UUID> registrarVenda(@RequestBody RegistrarVendaCommand command) {
        UUID vendaId = registrarVendaAnimalUseCase.executar(command);
        return ResponseEntity.status(201).body(vendaId);
    }

    @PostMapping("/animais/{animalId}/reverter")
    public ResponseEntity<Void> reverterVenda(@PathVariable UUID animalId) {
        reverterVendaAnimalUseCase.executar(animalId);
        return ResponseEntity.noContent().build();
    }
}
