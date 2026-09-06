package com.br.application.rest;

import com.br.application.dto.LoteResumoDTO;
import com.br.usecase.dto.AbrirLoteCommand;
import com.br.usecase.manejo.AbrirLoteUseCase;
import com.br.usecase.manejo.EncerrarLoteUseCase;
import com.br.usecase.financeiro.CalcularCustoArrobaUseCase;
import com.br.core.domain.model.Lote;
import com.br.core.domain.model.Pagina;
import com.br.core.domain.repository.LoteRepository;
import com.br.core.domain.repository.AnimalRepository;
import com.br.core.domain.repository.PesagemRepository;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/lotes")
@Tag(name = "Controle de Lote", description = "Métricas de arroba por lote")
public class LoteController {
    private final CalcularCustoArrobaUseCase calcularCustoArrobaUseCase;
    private final LoteRepository loteRepository;
    private final AbrirLoteUseCase abrirLoteUseCase;
    private final AnimalRepository animalRepository;
    private final PesagemRepository pesagemRepository;
    private final EncerrarLoteUseCase encerrarLoteUseCase;

    public LoteController(CalcularCustoArrobaUseCase calcularCustoArrobaUseCase,  LoteRepository loteRepository,
                          AbrirLoteUseCase abrirLoteUseCase, AnimalRepository animalRepository,
                          PesagemRepository pesagemRepository, EncerrarLoteUseCase encerrarLoteUseCase) {
        this.calcularCustoArrobaUseCase = calcularCustoArrobaUseCase;
        this.loteRepository = loteRepository;
        this.abrirLoteUseCase = abrirLoteUseCase;
        this.animalRepository = animalRepository;
        this.pesagemRepository = pesagemRepository;
        this.encerrarLoteUseCase = encerrarLoteUseCase;
    }
    public record LoteDTO(
            Long id,
            String nome,
            String descricao,
            Integer quantidadeAnimais,
            Double pesoMedio,
            String categoriaPredominante
    ) {}

    @GetMapping("/{loteId}/custo-arroba")
    public ResponseEntity<BigDecimal> obterCustoArrobaProduzida(@PathVariable UUID loteId) {
        BigDecimal custo = calcularCustoArrobaUseCase.executar(loteId);
        return ResponseEntity.ok(custo);
    }

    @PostMapping
    public ResponseEntity<UUID> abrirLote(@RequestBody AbrirLoteCommand command) {
        UUID novoLoteId = abrirLoteUseCase.executar(command);
        return ResponseEntity.status(201).body(novoLoteId);
    }

    @GetMapping
    public ResponseEntity<Pagina<LoteResumoDTO>> listarAtivos(@RequestParam(defaultValue = "0") int pagina, @RequestParam(defaultValue = "20") int tamanho) {
        Pagina<Lote> resultado = loteRepository.buscarTodosPaginado(pagina, tamanho);
        List<LoteResumoDTO> lotes = resultado.conteudo().stream()
                .map(this::paraResumo)
                .toList();
        return ResponseEntity.ok(new Pagina<>(lotes, resultado.numeroPagina(), resultado.tamanhoPagina(),
                resultado.totalElementos(), resultado.totalPaginas()));
    }

    private LoteResumoDTO paraResumo(Lote lote) {
        var animais = animalRepository.buscarPorLote(lote.getId());
        double pesoMedio = animais.stream()
                .map(animal -> pesagemRepository.buscarUltimaPesagemDoAnimal(animal.getId()))
                .flatMap(java.util.Optional::stream)
                .mapToDouble(pesagem -> pesagem.getPeso())
                .average()
                .orElse(0.0);
        return new LoteResumoDTO(lote.getId(), lote.getNome(), lote.getFase().name(), animais.size(), pesoMedio);
    }

    @PutMapping("/{id}/encerrar")
    public ResponseEntity<Void> encerrarLote(@PathVariable UUID id) {
        encerrarLoteUseCase.executar(id);
        return ResponseEntity.noContent().build();
    }



}
