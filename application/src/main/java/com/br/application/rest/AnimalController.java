package com.br.application.rest;

import com.br.usecase.dto.RegistrarNascimentoCommand;
import com.br.usecase.manejo.RegistrarNascimentoUseCase;
import com.br.core.domain.model.Animal;
import com.br.core.domain.model.Pagina;
import com.br.core.domain.repository.AnimalRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/animais")
public class AnimalController {
    private final RegistrarNascimentoUseCase registrarNascimentoUseCase;
    private final AnimalRepository animalRepository;

    public AnimalController(RegistrarNascimentoUseCase registrarNascimentoUseCase, AnimalRepository animalRepository) {
        this.registrarNascimentoUseCase = registrarNascimentoUseCase;
        this.animalRepository = animalRepository;
    }

    @PostMapping
    public ResponseEntity<UUID> cadastrar(@RequestBody RegistrarNascimentoCommand command) {
        UUID novoAnimalId = registrarNascimentoUseCase.executar(command);
        return ResponseEntity.status(201).body(novoAnimalId);
    }

    @GetMapping
    public ResponseEntity<Pagina<Animal>> listar(
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "10") int tamanho) {

        Pagina<Animal> resultado = animalRepository.buscarTodosPaginado(pagina, tamanho);
        return ResponseEntity.ok(resultado);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Animal> buscarPorId(@PathVariable UUID id) {
        return animalRepository.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/movimentar")
    public ResponseEntity<Void> trocarDeLote(@PathVariable UUID id, @RequestParam UUID novoLoteId) {
        // movimentarAnimalUseCase.executar(id, novoLoteId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}/baixa-morte")
    public ResponseEntity<Void> registrarMorte(@PathVariable UUID id) {
        // Exclusao lógica. O UseCase deve chamar animal.registrarMorte() e salvar.
        // Isso preserva os dados de GMD e CA do animal no histórico da safra.
        return ResponseEntity.noContent().build();
    }
}
