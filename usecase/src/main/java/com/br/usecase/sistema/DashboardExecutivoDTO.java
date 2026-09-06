package com.br.usecase.sistema;

import java.math.BigDecimal;

public class DashboardExecutivoDTO {
    // Financeiro
    private BigDecimal margemBrutaHectare;
    private BigDecimal pontoEquilibrioArrobas;
    private BigDecimal desembolsoCabecaMes;
    private BigDecimal custoArrobaProduzida;

    // Zootécnico
    private BigDecimal ganhoMedioDiarioGlobal;
    private BigDecimal conversaoAlimentarMedia;
    private BigDecimal taxaPrenhez;
    private BigDecimal taxaDesmame;
    private BigDecimal taxaLotacao;

    public DashboardExecutivoDTO(BigDecimal margemBrutaHectare, BigDecimal pontoEquilibrioArrobas,
                                 BigDecimal desembolsoCabecaMes, BigDecimal custoArrobaProduzida,
                                 BigDecimal ganhoMedioDiarioGlobal,
                                 BigDecimal taxaPrenhez, BigDecimal taxaDesmame, BigDecimal taxaLotacao) {
        this.margemBrutaHectare = margemBrutaHectare;
        this.pontoEquilibrioArrobas = pontoEquilibrioArrobas;
        this.desembolsoCabecaMes = desembolsoCabecaMes;
        this.custoArrobaProduzida = custoArrobaProduzida;
        this.ganhoMedioDiarioGlobal = ganhoMedioDiarioGlobal;
        this.conversaoAlimentarMedia = conversaoAlimentarMedia;
        this.taxaPrenhez = taxaPrenhez;
        this.taxaDesmame = taxaDesmame;
        this.taxaLotacao = taxaLotacao;
    }

    public BigDecimal getMargemBrutaHectare() { return margemBrutaHectare; }
    public BigDecimal getPontoEquilibrioArrobas() { return pontoEquilibrioArrobas; }
    public BigDecimal getDesembolsoCabecaMes() { return desembolsoCabecaMes; }
    public BigDecimal getCustoArrobaProduzida() { return custoArrobaProduzida; }
    public BigDecimal getGanhoMedioDiarioGlobal() { return ganhoMedioDiarioGlobal; }
    public BigDecimal getConversaoAlimentarMedia() { return conversaoAlimentarMedia; }
    public BigDecimal getTaxaPrenhez() { return taxaPrenhez; }
    public BigDecimal getTaxaDesmame() { return taxaDesmame; }
    public BigDecimal getTaxaLotacao() { return taxaLotacao; }
}
