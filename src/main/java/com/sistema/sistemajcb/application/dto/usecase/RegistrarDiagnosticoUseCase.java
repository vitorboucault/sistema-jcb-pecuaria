package com.sistema.sistemajcb.application.dto.usecase;

import com.sistema.sistemajcb.domain.enums.ResultadoDiagnostico;
import com.sistema.sistemajcb.domain.model.DiagnosticoGestacao;
import com.sistema.sistemajcb.domain.repository.DiagnosticoGestacaoRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.UUID;

@Service
public class RegistrarDiagnosticoUseCase {

    private final DiagnosticoGestacaoRepository diagnosticoRepository;

    public RegistrarDiagnosticoUseCase(DiagnosticoGestacaoRepository diagnosticoRepository) {
        this.diagnosticoRepository = diagnosticoRepository;
    }

    public DiagnosticoGestacao executar(UUID animalId, UUID estacaoMontaId, LocalDate dataDiagnostico, ResultadoDiagnostico resultado, LocalDate dataInseminacao) {
        DiagnosticoGestacao diagnostico = new DiagnosticoGestacao(
                UUID.randomUUID(), animalId, estacaoMontaId, dataDiagnostico, resultado
        );

        diagnostico.projetarParto(dataInseminacao);
        diagnosticoRepository.salvar(diagnostico);
        return diagnostico;

    }
}
