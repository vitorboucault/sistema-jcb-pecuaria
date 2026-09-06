package com.br.usecase.sistema;

import java.math.BigDecimal;

public class DashboardExecutivoDTO {

    private final BigDecimal margemBrutaHectare;
    private final BigDecimal pontoEquilibrioArrobas;
    private final BigDecimal desembolsoCabecaMes;
    private final BigDecimal custoArrobaProduzida;

    private final BigDecimal ganhoMedioDiarioGlobal;
    private final BigDecimal conversaoAlimentarMedia;
    private final BigDecimal taxaPrenhez;
    private final BigDecimal taxaDesmame;
    private final BigDecimal taxaLotacao;

    public DashboardExecutivoDTO(
            BigDecimal margemBrutaHectare,
            BigDecimal pontoEquilibrioArrobas,
            BigDecimal desembolsoCabecaMes,
            BigDecimal custoArrobaProduzida,
            BigDecimal ganhoMedioDiarioGlobal,
            BigDecimal conversaoAlimentarMedia,
            BigDecimal taxaPrenhez,
            BigDecimal taxaDesmame,
            BigDecimal taxaLotacao
    ) {
        this.margemBrutaHectare = valorOuZero(margemBrutaHectare);
        this.pontoEquilibrioArrobas = valorOuZero(pontoEquilibrioArrobas);
        this.desembolsoCabecaMes = valorOuZero(desembolsoCabecaMes);
        this.custoArrobaProduzida = valorOuZero(custoArrobaProduzida);

        this.ganhoMedioDiarioGlobal = valorOuZero(ganhoMedioDiarioGlobal);
        this.conversaoAlimentarMedia = valorOuZero(conversaoAlimentarMedia);
        this.taxaPrenhez = valorOuZero(taxaPrenhez);
        this.taxaDesmame = valorOuZero(taxaDesmame);
        this.taxaLotacao = valorOuZero(taxaLotacao);
    }

    private static BigDecimal valorOuZero(BigDecimal valor) {
        return valor != null ? valor : BigDecimal.ZERO;
    }

    public BigDecimal getMargemBrutaHectare() {
        return margemBrutaHectare;
    }

    public BigDecimal getPontoEquilibrioArrobas() {
        return pontoEquilibrioArrobas;
    }

    public BigDecimal getDesembolsoCabecaMes() {
        return desembolsoCabecaMes;
    }

    public BigDecimal getCustoArrobaProduzida() {
        return custoArrobaProduzida;
    }

    public BigDecimal getGanhoMedioDiarioGlobal() {
        return ganhoMedioDiarioGlobal;
    }

    public BigDecimal getConversaoAlimentarMedia() {
        return conversaoAlimentarMedia;
    }

    public BigDecimal getTaxaPrenhez() {
        return taxaPrenhez;
    }

    public BigDecimal getTaxaDesmame() {
        return taxaDesmame;
    }

    public BigDecimal getTaxaLotacao() {
        return taxaLotacao;
    }
}