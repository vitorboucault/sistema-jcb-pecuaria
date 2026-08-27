package com.sistema.sistemajcb.application.dto.usecase;

import com.sistema.sistemajcb.domain.model.MovimentacaoLote;
import com.sistema.sistemajcb.domain.repository.MovimentacaoLoteRepository;
import com.sistema.sistemajcb.domain.repository.PastoRepository;
import com.sistema.sistemajcb.domain.repository.PesagemRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.UUID;

@Service
public class CalcularTaxaLotacaoUseCase {
    private final MovimentacaoLoteRepository movimentacaoRepository;
    private final PastoRepository pastoRepository;
    private final PesagemRepository pesagemRepository;

    private static final double PESO_UNIDADE_ANIMAL = 450.0;

    public CalcularTaxaLotacaoUseCase(MovimentacaoLoteRepository movimentacaoRepository, PastoRepository pastoRepository, PesagemRepository pesagemRepository) {
        this.movimentacaoRepository = movimentacaoRepository;
        this.pastoRepository = pastoRepository;
        this.pesagemRepository = pesagemRepository;
    }

    public BigDecimal executar(UUID pastoId) {
        Double areaHectares = pastoRepository.buscarAreaHectares(pastoId);

        if (areaHectares == null || areaHectares <= 0) {
            return BigDecimal.ZERO; // Evita divisão por zero
        }

        // 1. Descobrir quais lotes estão neste pasto agora
        List<MovimentacaoLote> lotesAtivos = movimentacaoRepository.buscarLotesAtivosNoPasto(pastoId);

        if (lotesAtivos.isEmpty()) {
            return BigDecimal.ZERO; // Pasto descansando (vazio)
        }

        double taxaLotacaoUaPorHa = getTaxaLotacaoUaPorHa(lotesAtivos, areaHectares);

        return BigDecimal.valueOf(taxaLotacaoUaPorHa).setScale(2, RoundingMode.HALF_UP);
    }

    private static double getTaxaLotacaoUaPorHa(List<MovimentacaoLote> lotesAtivos, Double areaHectares) {
        double pesoTotalVivoNoPasto = 0.0;

        // 2. Somar o peso vivo de todos os animais desses lotes
        for (MovimentacaoLote mov : lotesAtivos) {
            // Mock do cálculo que busca a soma do último peso de todos os animais do lote:
            // pesoTotalVivoNoPasto += pesagemRepository.calcularPesoTotalDoLote(mov.getLoteId());
            pesoTotalVivoNoPasto += 13500.0; // Exemplo: Lote de 30 cabeças x 450kg
        }

        // 3. Converter Peso Vivo em UA e dividir pelos Hectares
        double totalUA = pesoTotalVivoNoPasto / PESO_UNIDADE_ANIMAL;
        double taxaLotacaoUaPorHa = totalUA / areaHectares;
        return taxaLotacaoUaPorHa;
    }

}
