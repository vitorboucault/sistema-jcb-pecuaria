package com.br.infra.persistence.adapter;

import com.br.core.domain.model.DiagnosticoGestacao;
import com.br.core.domain.repository.DiagnosticoGestacaoRepository;
import com.br.infra.persistence.mapper.DiagnosticoGestacaoMapper;
import com.br.infra.persistence.repository.SpringDataDiagnosticoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class DiagnosticoGestacaoRepositoryImpl implements DiagnosticoGestacaoRepository {
    private final SpringDataDiagnosticoRepository springData;
    private final DiagnosticoGestacaoMapper mapper;

    public DiagnosticoGestacaoRepositoryImpl(SpringDataDiagnosticoRepository springData, DiagnosticoGestacaoMapper mapper) {
        this.springData = springData;
        this.mapper = mapper;
    }

    @Override
    public void salvar(DiagnosticoGestacao diagnostico) {
        springData.save(mapper.toEntity(diagnostico));
    }

    @Override
    public List<DiagnosticoGestacao> buscarPorEstacaoMonta(UUID estacaoMontaId) {
        return springData.findByEstacaoMontaId(estacaoMontaId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
}
