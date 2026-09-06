package com.br.core.domain.model;

import com.br.core.domain.enums.CondicaoNascimento;
import com.br.core.domain.enums.TipoParto;

import java.time.LocalDate;
import java.util.UUID;

public class Parto {
        private UUID id;
        private UUID matrizId;
        private UUID bezerroId; // Pode ser null se nasceu morto
        private UUID estacaoMontaId; // Referência de qual safra este parto pertence
        private LocalDate dataParto;
        private TipoParto tipo;
        private CondicaoNascimento condicao;
        private Double pesoNascimentoKg;

        public Parto(UUID id, UUID matrizId, UUID bezerroId, UUID estacaoMontaId, LocalDate dataParto, TipoParto tipo, CondicaoNascimento condicao, Double pesoNascimentoKg) {
            this.id = id;
            this.matrizId = matrizId;
            this.bezerroId = bezerroId;
            this.estacaoMontaId = estacaoMontaId;
            this.dataParto = dataParto;
            this.tipo = tipo;
            this.condicao = condicao;
            this.pesoNascimentoKg = pesoNascimentoKg;
        }

        public boolean isBezerroVivo() {
            return this.condicao == CondicaoNascimento.VIVO;
        }

    public UUID getId() {return id;}
    public UUID getMatrizId() {return matrizId;}
    public UUID getBezerroId() {return bezerroId;}
    public UUID getEstacaoMontaId() {return estacaoMontaId;}
    public LocalDate getDataParto() {return dataParto;}
    public TipoParto getTipo() {return tipo;}
    public CondicaoNascimento getCondicao() {return condicao;}
    public Double getPesoNascimentoKg() {return pesoNascimentoKg;}
}
