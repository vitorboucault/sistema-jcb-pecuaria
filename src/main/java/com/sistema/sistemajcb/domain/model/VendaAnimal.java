package com.sistema.sistemajcb.domain.model;

import com.sistema.sistemajcb.domain.enums.ModalidadeVenda;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.UUID;

import static com.sistema.sistemajcb.domain.enums.ModalidadeVenda.*;

public class VendaAnimal {
        private UUID id;
        private UUID animalId;
        private LocalDate dataVenda;
        private ModalidadeVenda modalidade;
        private Double pesoVivoKg;
        private Double rendimentoCarcacaPercentual; // Pode ser null se não for frigorífico
        private BigDecimal precoAcordado; // Preço da @ ou preço por cabeça

        public VendaAnimal(UUID id, UUID animalId, LocalDate dataVenda, ModalidadeVenda modalidade, Double pesoVivoKg, Double rendimentoCarcacaPercentual, BigDecimal precoAcordado) {
            this.id = id;
            this.animalId = animalId;
            this.dataVenda = dataVenda;
            this.modalidade = modalidade;
            this.pesoVivoKg = pesoVivoKg;
            this.rendimentoCarcacaPercentual = rendimentoCarcacaPercentual;
            this.precoAcordado = precoAcordado;
        }

    public BigDecimal calcularReceitaTotal() {
        double totalArrobas = 0.0;

        switch (this.modalidade) {
            case FRIGORIFICO:
                if (rendimentoCarcacaPercentual == null) {
                    throw new IllegalArgumentException("Venda para frigorífico exige rendimento de carcaça.");
                }
                double pesoCarcaca = this.pesoVivoKg * (this.rendimentoCarcacaPercentual / 100.0);
                totalArrobas = pesoCarcaca / 15.0;
                return this.precoAcordado.multiply(BigDecimal.valueOf(totalArrobas))
                        .setScale(2, RoundingMode.HALF_UP);

            case PESO:
                totalArrobas = this.pesoVivoKg / 30.0;
                return this.precoAcordado.multiply(BigDecimal.valueOf(totalArrobas))
                        .setScale(2, RoundingMode.HALF_UP);

            case POR_CABECA:

                return this.precoAcordado.setScale(2, RoundingMode.HALF_UP);

            default:
                return BigDecimal.ZERO;
        }
    }

    public UUID getId() {return id;}
    public UUID getAnimalId() {return animalId;}
    public LocalDate getDataVenda() {return dataVenda;}
    public ModalidadeVenda getModalidade() {return modalidade;}
    public Double getPesoVivoKg() {return pesoVivoKg;}
    public Double getRendimentoCarcacaPercentual() {return rendimentoCarcacaPercentual;}
    public BigDecimal getPrecoAcordado() {return precoAcordado;}
}
