package com.br.usecase.zootecnico;

import com.br.core.domain.enums.ResultadoDiagnostico;
import com.br.core.domain.model.DiagnosticoGestacao;
import com.br.core.domain.repository.DiagnosticoGestacaoRepository;
import jakarta.inject.Named;

import java.time.LocalDate;
import java.util.UUID;

@Named
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
