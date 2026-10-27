package com.br.application.rest;

import com.br.application.dto.LancamentoFinanceiroResponse;
import com.br.application.dto.RegistrarLancamentoFinanceiroRequest;
import com.br.application.dto.ConfirmarLancamentoFinanceiroRequest;
import com.br.usecase.financeiro.ConfirmarPagamentoUseCase;
import com.br.usecase.financeiro.ConfirmarRecebimentoUseCase;
import com.br.usecase.financeiro.ConsultarLancamentoFinanceiroUseCase;
import com.br.usecase.financeiro.RegistrarLancamentoFinanceiroUseCase;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/financeiro/lancamentos")
public class FinanceiroController {
    private final RegistrarLancamentoFinanceiroUseCase registrar;
    private final ConsultarLancamentoFinanceiroUseCase consultar;
    private final ConfirmarPagamentoUseCase confirmarPagamento;
    private final ConfirmarRecebimentoUseCase confirmarRecebimento;

    public FinanceiroController(RegistrarLancamentoFinanceiroUseCase registrar,
                                ConsultarLancamentoFinanceiroUseCase consultar,
                                ConfirmarPagamentoUseCase confirmarPagamento,
                                ConfirmarRecebimentoUseCase confirmarRecebimento) {
        this.registrar = registrar;
        this.consultar = consultar;
        this.confirmarPagamento = confirmarPagamento;
        this.confirmarRecebimento = confirmarRecebimento;
    }

    @PostMapping
    public ResponseEntity<LancamentoFinanceiroResponse> cadastrar(@Valid @RequestBody RegistrarLancamentoFinanceiroRequest request) {
        var resposta = LancamentoFinanceiroResponse.from(registrar.executar(request.toCommand()));
        return ResponseEntity.created(URI.create("/api/v1/financeiro/lancamentos/" + resposta.id())).body(resposta);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LancamentoFinanceiroResponse> buscarPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(LancamentoFinanceiroResponse.from(consultar.executar(id)));
    }

    @PostMapping("/{id}/pagar")
    public ResponseEntity<LancamentoFinanceiroResponse> pagar(@PathVariable UUID id,
            @Valid @RequestBody ConfirmarLancamentoFinanceiroRequest request) {
        return ResponseEntity.ok(LancamentoFinanceiroResponse.from(confirmarPagamento.executar(id, request.dataEfetivacao())));
    }

    @PostMapping("/{id}/receber")
    public ResponseEntity<LancamentoFinanceiroResponse> receber(@PathVariable UUID id,
            @Valid @RequestBody ConfirmarLancamentoFinanceiroRequest request) {
        return ResponseEntity.ok(LancamentoFinanceiroResponse.from(confirmarRecebimento.executar(id, request.dataEfetivacao())));
    }
}
