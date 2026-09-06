package com.br.usecase.sistema;

import com.br.core.domain.model.Animal;
import com.br.usecase.nutricao.CalcularConversaoAlimentarUseCase;
import com.br.usecase.sistema.DashboardExecutivoDTO;
import com.br.core.domain.repository.AnimalRepository;
import com.br.usecase.financeiro.CalcularCustoArrobaUseCase;
import com.br.usecase.financeiro.CalcularDesembolsoCabecaMesUseCase;
import com.br.usecase.financeiro.CalcularGmdGlobalUseCase;
import com.br.usecase.financeiro.CalcularMargemBrutaHectareUseCase;
import com.br.usecase.zootecnico.CalcularPontoEquilibrioUseCase;
import com.br.usecase.zootecnico.CalcularTaxaDesmameUseCase;
import com.br.usecase.zootecnico.CalcularTaxaLotacaoUseCase;
import com.br.usecase.zootecnico.CalcularTaxaPrenhezUseCase;
import jakarta.inject.Named;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Named
public class GerarDashboardExecutivoUseCase {

    private final CalcularMargemBrutaHectareUseCase margemBrutaUseCase;
    private final CalcularPontoEquilibrioUseCase pontoEquilibrioUseCase;
    private final CalcularDesembolsoCabecaMesUseCase desembolsoUseCase;
    private final CalcularCustoArrobaUseCase custoArrobaUseCase;
    private final CalcularGmdGlobalUseCase gmdGlobalUseCase;
    private final  CalcularConversaoAlimentarUseCase conversaoAlimentarUseCase;
    private final CalcularTaxaPrenhezUseCase taxaPrenhezUseCase;
    private final CalcularTaxaDesmameUseCase taxaDesmameUseCase;
    private final CalcularTaxaLotacaoUseCase taxaLotacaoUseCase;
    private final AnimalRepository animalRepository;

    public GerarDashboardExecutivoUseCase(
            CalcularMargemBrutaHectareUseCase margemBrutaUseCase,
            CalcularPontoEquilibrioUseCase pontoEquilibrioUseCase,
            CalcularDesembolsoCabecaMesUseCase desembolsoUseCase,
            CalcularCustoArrobaUseCase custoArrobaUseCase,
            CalcularGmdGlobalUseCase gmdGlobalUseCase,
            CalcularConversaoAlimentarUseCase conversaoAlimentarUseCase,
            CalcularTaxaPrenhezUseCase taxaPrenhezUseCase,
            CalcularTaxaDesmameUseCase taxaDesmameUseCase,
            CalcularTaxaLotacaoUseCase taxaLotacaoUseCase,
            AnimalRepository animalRepository) {
        this.margemBrutaUseCase = margemBrutaUseCase;
        this.pontoEquilibrioUseCase = pontoEquilibrioUseCase;
        this.desembolsoUseCase = desembolsoUseCase;
        this.custoArrobaUseCase = custoArrobaUseCase;
        this.gmdGlobalUseCase = gmdGlobalUseCase;
        this.conversaoAlimentarUseCase = conversaoAlimentarUseCase;
        this.taxaPrenhezUseCase = taxaPrenhezUseCase;
        this.taxaDesmameUseCase = taxaDesmameUseCase;
        this.taxaLotacaoUseCase = taxaLotacaoUseCase;
        this.animalRepository = animalRepository;
    }

    public DashboardExecutivoDTO executar(LocalDate inicioSafra, LocalDate fimSafra, BigDecimal precoArrobaHoje,
                                          UUID estacaoMontaId, UUID pastoReferenciaId, UUID loteReferenciaId) {

        List<UUID> rebanhoAtivoIds = animalRepository.buscarAnimaisElegiveisParaEvolucao()
                .stream().map(Animal::getId).collect(Collectors.toList());

        BigDecimal margemBruta = margemBrutaUseCase.executar(inicioSafra, fimSafra);
        BigDecimal pontoEquilibrio = pontoEquilibrioUseCase.executar(inicioSafra, fimSafra, precoArrobaHoje);
        BigDecimal desembolso = desembolsoUseCase.executar(inicioSafra, fimSafra);
        BigDecimal custoArroba = custoArrobaUseCase.executar(inicioSafra, fimSafra);

        BigDecimal gmdGlobal = gmdGlobalUseCase.executar(inicioSafra, fimSafra, rebanhoAtivoIds);
        BigDecimal caMedia = BigDecimal.ZERO;
        BigDecimal taxaPrenhez = estacaoMontaId != null ? taxaPrenhezUseCase.executar(estacaoMontaId) : BigDecimal.ZERO;
        BigDecimal taxaDesmame = estacaoMontaId != null ? taxaDesmameUseCase.executar(estacaoMontaId) : BigDecimal.ZERO;
        BigDecimal taxaLotacao = pastoReferenciaId != null ? taxaLotacaoUseCase.executar(pastoReferenciaId) : BigDecimal.ZERO;

        return new DashboardExecutivoDTO(
                margemBruta,
                pontoEquilibrio,
                desembolso,
                custoArroba,
                gmdGlobal,
                caMedia,
                taxaPrenhez,
                taxaDesmame,
                taxaLotacao
        );
    }
}