package com.br.application.rest;

import com.br.application.dto.AnimalResumoDTO;
import com.br.application.dto.AnimalInputDTO;
import com.br.core.domain.enums.Categoria;
import com.br.core.domain.enums.Status;
import com.br.usecase.dto.RegistrarNascimentoCommand;
import com.br.usecase.dto.RegistrarPesagemCommand;
import com.br.usecase.manejo.RegistrarNascimentoUseCase;
import com.br.core.domain.model.Animal;
import com.br.core.domain.model.Pagina;
import com.br.core.domain.repository.AnimalRepository;
import com.br.core.domain.repository.LoteRepository;
import com.br.core.domain.repository.PesagemRepository;
import com.br.core.domain.enums.Sexo;
import com.br.usecase.manejo.RegistrarPesagemUseCase;
import com.br.usecase.manejo.MovimentarAnimalUseCase;
import com.br.usecase.manejo.RegistrarMorteAnimalUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.transaction.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/animais")
public class AnimalController {
    private final RegistrarNascimentoUseCase registrarNascimentoUseCase;
    private final AnimalRepository animalRepository;
    private final RegistrarPesagemUseCase registrarPesagemUseCase;
    private final LoteRepository loteRepository;
    private final PesagemRepository pesagemRepository;
    private final MovimentarAnimalUseCase movimentarAnimalUseCase;
    private final RegistrarMorteAnimalUseCase registrarMorteAnimalUseCase;

    public AnimalController(
            RegistrarNascimentoUseCase registrarNascimentoUseCase,
            RegistrarPesagemUseCase registrarPesagemUseCase,
            AnimalRepository animalRepository,
            LoteRepository loteRepository,
            PesagemRepository pesagemRepository,
            MovimentarAnimalUseCase movimentarAnimalUseCase,
            RegistrarMorteAnimalUseCase registrarMorteAnimalUseCase) {
        this.registrarNascimentoUseCase = registrarNascimentoUseCase;
        this.registrarPesagemUseCase = registrarPesagemUseCase;
        this.animalRepository = animalRepository;
        this.loteRepository = loteRepository;
        this.pesagemRepository = pesagemRepository;
        this.movimentarAnimalUseCase = movimentarAnimalUseCase;
        this.registrarMorteAnimalUseCase = registrarMorteAnimalUseCase;
    }

    @PostMapping
    @Transactional
    public ResponseEntity<UUID> cadastrarAnimal(@RequestBody AnimalInputDTO dto) {
        UUID animalId;
        UUID loteUuid = null;

        if (dto.loteId() != null && !dto.loteId().isBlank() && !"0".equals(dto.loteId())) {
            loteUuid = UUID.fromString(dto.loteId());
        }
        if ("NASCIMENTO".equalsIgnoreCase(dto.origem())) {
            RegistrarNascimentoCommand command = new RegistrarNascimentoCommand(
                    dto.brincoRgd(),
                    dto.dataNascimento(),
                    Sexo.valueOf(dto.sexo()),
                    dto.maeId(),
                    loteUuid
            );
            animalId = registrarNascimentoUseCase.executar(command);
            registrarPesagemInicial(animalId, dto.peso(), dto.dataEntrada());
        } else {
            Animal animalCompra = new Animal(
                    UUID.randomUUID(),
                    dto.brincoRgd(),
                    dto.dataNascimento(),
                    Sexo.valueOf(dto.sexo()),
                    Categoria.valueOf(dto.categoria()),
                    Status.ATIVO,
                    null,
                    loteUuid
            );
            Animal salvo = animalRepository.salvar(animalCompra);
            animalId = salvo.getId();

            registrarPesagemInicial(animalId, dto.peso(), dto.dataEntrada());
        }
        return ResponseEntity.status(201).body(animalId);
    }

    @GetMapping
    public ResponseEntity<Pagina<AnimalResumoDTO>> listar(
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "10") int tamanho) {

        Pagina<Animal> resultado = animalRepository.buscarTodosPaginado(pagina, tamanho);
        List<AnimalResumoDTO> animais = resultado.conteudo().stream()
                .map(this::paraResumo)
                .toList();
        return ResponseEntity.ok(new Pagina<>(animais, resultado.numeroPagina(), resultado.tamanhoPagina(),
                resultado.totalElementos(), resultado.totalPaginas()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Animal> buscarPorId(@PathVariable UUID id) {
        return animalRepository.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    private AnimalResumoDTO paraResumo(Animal animal) {
        String nomeLote = animal.getLoteId() == null ? null : loteRepository.buscarPorId(animal.getLoteId())
                .map(lote -> lote.getNome())
                .orElse(null);
        Double pesoAtual = pesagemRepository.buscarUltimaPesagemDoAnimal(animal.getId())
                .map(pesagem -> pesagem.getPeso())
                .orElse(null);

        return new AnimalResumoDTO(
                animal.getId(), animal.getBrincoRgd(), animal.getCategoriaAtual().name(), animal.getSexo().name(),
                animal.getLoteId(), nomeLote, pesoAtual, animal.getStatus().name(), animal.getDataNascimento());
    }

    private void registrarPesagemInicial(UUID animalId, Double peso, LocalDate dataPesagem) {
        if (peso == null || peso <= 0) {
            return;
        }
        if (dataPesagem == null) {
            throw new IllegalArgumentException("A data da pesagem inicial é obrigatória.");
        }
        registrarPesagemUseCase.executar(new RegistrarPesagemCommand(animalId, dataPesagem, peso, true));
    }

    @PutMapping("/{id}/movimentar")
    public ResponseEntity<Void> trocarDeLote(@PathVariable UUID id, @RequestParam UUID novoLoteId) {
        movimentarAnimalUseCase.executar(id, novoLoteId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}/baixa-morte")
    public ResponseEntity<Void> registrarMorte(@PathVariable UUID id) {
        registrarMorteAnimalUseCase.executar(id);
        return ResponseEntity.noContent().build();
    }

}
