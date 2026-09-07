package com.br.application.rest;

import com.br.core.domain.enums.OrigemPesagem;
import com.br.application.dto.AtualizarAnimalRequest;
import com.br.application.dto.AnimalResumoDTO;
import com.br.application.dto.AnimalInputDTO;
import com.br.core.domain.enums.Categoria;
import com.br.usecase.dto.AtualizarAnimalCommand;
import com.br.usecase.dto.RegistrarCompraAnimalCommand;
import com.br.usecase.dto.RegistrarMorteAnimalCommand;
import com.br.usecase.dto.RegistrarNascimentoCommand;
import com.br.usecase.dto.RegistrarPesagemCommand;
import com.br.usecase.dto.ResumoRebanhoDTO;
import com.br.usecase.manejo.AtualizarAnimalUseCase;
import com.br.usecase.manejo.ExcluirAnimalUseCase;
import com.br.usecase.manejo.ObterResumoRebanhoUseCase;
import com.br.usecase.manejo.RegistrarCompraAnimalUseCase;
import com.br.usecase.manejo.RegistrarNascimentoUseCase;
import com.br.core.domain.model.Animal;
import com.br.core.domain.model.Lote;
import com.br.core.domain.model.Pagina;
import com.br.core.domain.model.Pesagem;
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
import jakarta.validation.Valid;


import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
    private final RegistrarCompraAnimalUseCase registrarCompraAnimalUseCase;
    private final ObterResumoRebanhoUseCase obterResumoRebanhoUseCase;
    private final AtualizarAnimalUseCase atualizarAnimalUseCase;
    private final ExcluirAnimalUseCase excluirAnimalUseCase;

    public AnimalController(
            RegistrarNascimentoUseCase registrarNascimentoUseCase,
            RegistrarPesagemUseCase registrarPesagemUseCase,
            AnimalRepository animalRepository,
            LoteRepository loteRepository,
            PesagemRepository pesagemRepository,
            MovimentarAnimalUseCase movimentarAnimalUseCase,
            RegistrarMorteAnimalUseCase registrarMorteAnimalUseCase,
            RegistrarCompraAnimalUseCase registrarCompraAnimalUseCase,
            ObterResumoRebanhoUseCase obterResumoRebanhoUseCase,
            AtualizarAnimalUseCase atualizarAnimalUseCase,
            ExcluirAnimalUseCase excluirAnimalUseCase) {
        this.registrarNascimentoUseCase = registrarNascimentoUseCase;
        this.registrarPesagemUseCase = registrarPesagemUseCase;
        this.animalRepository = animalRepository;
        this.loteRepository = loteRepository;
        this.pesagemRepository = pesagemRepository;
        this.movimentarAnimalUseCase = movimentarAnimalUseCase;
        this.registrarMorteAnimalUseCase = registrarMorteAnimalUseCase;
        this.registrarCompraAnimalUseCase = registrarCompraAnimalUseCase;
        this.obterResumoRebanhoUseCase = obterResumoRebanhoUseCase;
        this.atualizarAnimalUseCase = atualizarAnimalUseCase;
        this.excluirAnimalUseCase = excluirAnimalUseCase;
    }

    @PostMapping
    @Transactional
    public ResponseEntity<UUID> cadastrarAnimal(
            @Valid @RequestBody AnimalInputDTO dto
    ) {
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
            RegistrarCompraAnimalCommand command = new RegistrarCompraAnimalCommand(
                    dto.brincoRgd(),
                    dto.dataNascimento(),
                    Sexo.valueOf(dto.sexo()),
                    Categoria.valueOf(dto.categoria()),
                    loteUuid,
                    dto.dataCompra(),
                    dto.valorCompra()
            );
            animalId = registrarCompraAnimalUseCase.executar(command);

            registrarPesagemInicial(animalId, dto.peso(), dto.dataEntrada());
        }
        return ResponseEntity.status(201).body(animalId);
    }

    @GetMapping
    public ResponseEntity<Pagina<AnimalResumoDTO>> listar(
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "10") int tamanho) {

        Pagina<Animal> resultado = animalRepository.buscarTodosPaginado(pagina, tamanho);
        List<Animal> animaisDaPagina = resultado.conteudo();
        Map<UUID, Lote> lotesPorId = buscarLotesDaPagina(animaisDaPagina);
        Map<UUID, Pesagem> ultimasPesagensPorAnimalId = buscarUltimasPesagensDaPagina(animaisDaPagina);

        List<AnimalResumoDTO> animais = animaisDaPagina.stream()
                .map(animal -> paraResumo(animal, lotesPorId, ultimasPesagensPorAnimalId))
                .toList();
        return ResponseEntity.ok(new Pagina<>(animais, resultado.numeroPagina(), resultado.tamanhoPagina(),
                resultado.totalElementos(), resultado.totalPaginas()));
    }

    @GetMapping("/resumo")
    public ResponseEntity<ResumoRebanhoDTO> obterResumo() {
        return ResponseEntity.ok(obterResumoRebanhoUseCase.executar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Animal> buscarPorId(@PathVariable UUID id) {
        return animalRepository.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> atualizar(@PathVariable UUID id, @Valid @RequestBody AtualizarAnimalRequest request) {
        atualizarAnimalUseCase.executar(new AtualizarAnimalCommand(
                id,
                request.brincoRgd(),
                request.dataNascimento(),
                request.sexo(),
                request.categoria()
        ));
        return ResponseEntity.noContent().build();
    }

    private Map<UUID, Lote> buscarLotesDaPagina(List<Animal> animais) {
        List<UUID> loteIds = animais.stream()
                .map(Animal::getLoteId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();

        return loteRepository.buscarPorIds(loteIds);
    }

    private Map<UUID, Pesagem> buscarUltimasPesagensDaPagina(List<Animal> animais) {
        List<UUID> animalIds = animais.stream()
                .map(Animal::getId)
                .toList();

        return pesagemRepository.buscarUltimasPesagensPorAnimalIds(animalIds);
    }

    private AnimalResumoDTO paraResumo(Animal animal, Map<UUID, Lote> lotesPorId, Map<UUID, Pesagem> ultimasPesagensPorAnimalId) {
        Lote lote = animal.getLoteId() == null ? null : lotesPorId.get(animal.getLoteId());
        Pesagem ultimaPesagem = ultimasPesagensPorAnimalId.get(animal.getId());
        String nomeLote = lote == null ? null : lote.getNome();
        Double pesoAtual = ultimaPesagem == null ? null : ultimaPesagem.getPeso();

        return new AnimalResumoDTO(
                animal.getId(), animal.getBrincoRgd(), animal.getCategoriaAtual().name(), animal.getSexo().name(),
                animal.getLoteId(), nomeLote, pesoAtual, animal.getStatus().name(), animal.getDataNascimento(),
                animal.getDataMorte());
    }

    private void registrarPesagemInicial(UUID animalId, Double peso, LocalDate dataPesagem) {
        if (peso == null || peso <= 0) {
            return;
        }
        if (dataPesagem == null) {
            throw new IllegalArgumentException("A data da pesagem inicial é obrigatória.");
        }
        registrarPesagemUseCase.executar(new RegistrarPesagemCommand(
                animalId, dataPesagem, peso, true, OrigemPesagem.CADASTRO_INICIAL));
    }

    @PutMapping("/{id}/movimentar")
    public ResponseEntity<Void> trocarDeLote(@PathVariable UUID id, @RequestParam UUID novoLoteId) {
        movimentarAnimalUseCase.executar(id, novoLoteId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}/baixa-morte")
    public ResponseEntity<Void> registrarMorte(@PathVariable UUID id, @RequestParam LocalDate dataMorte) {
        registrarMorteAnimalUseCase.executar(new RegistrarMorteAnimalCommand(id, dataMorte));
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable UUID id) {
        excluirAnimalUseCase.executar(id);
        return ResponseEntity.noContent().build();
    }

}
