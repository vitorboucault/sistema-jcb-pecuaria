package com.sistema.sistemajcb.domain.model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

public class Desmame {
        private UUID id;
        private UUID bezerroId;
        private UUID estacaoMontaId;
        private LocalDate dataNascimento;
        private LocalDate dataDesmame;
        private Double pesoNascimento;
        private Double pesoDesmame;

    public Desmame(UUID id, UUID bezerroId, UUID estacaoMontaId, LocalDate dataNascimento, LocalDate dataDesmame, Double pesoNascimento, Double pesoDesmame) {
        this.id = id;
        this.bezerroId = bezerroId;
        this.estacaoMontaId = estacaoMontaId;
        this.dataNascimento = dataNascimento;
        this.dataDesmame = dataDesmame;
        this.pesoNascimento = pesoNascimento;
        this.pesoDesmame = pesoDesmame;
    }

    public Double calcularPesoAjustado205Dias() {
            long diasDeVida = ChronoUnit.DAYS.between(dataNascimento, dataDesmame);
            if (diasDeVida == 0) return pesoDesmame;

            double ganhoDiarioDoBezerro = (pesoDesmame - pesoNascimento) / diasDeVida;
            return (ganhoDiarioDoBezerro * 205) + pesoNascimento;
    }

    public UUID getId() {return id;}
    public UUID getBezerroId() {return bezerroId;}
    public UUID getEstacaoMontaId() {return estacaoMontaId;}
    public LocalDate getDataNascimento() {return dataNascimento;}
    public LocalDate getDataDesmame() {return dataDesmame;}
    public Double getPesoNascimento() {return pesoNascimento;}
    public Double getPesoDesmame() {return pesoDesmame;}
}
