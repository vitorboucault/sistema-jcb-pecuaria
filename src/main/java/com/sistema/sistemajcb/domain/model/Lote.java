package com.sistema.sistemajcb.domain.model;

import com.sistema.sistemajcb.domain.enums.FaseLote;

import java.time.LocalDate;
import java.util.UUID;

public class Lote {

    private final UUID id;
    private String nome;
    private FaseLote fase;
    private LocalDate dataFormacao;
    private LocalDate dataEncerramento;

    public Lote(String nome, FaseLote fase) {
        this.id = UUID.randomUUID();
        this.nome = nome;
        this.fase = fase;
        this.dataFormacao = LocalDate.now();
    }

    public Lote(UUID id, String nome, FaseLote fase, LocalDate dataFormacao, LocalDate dataEncerramento) {
        this.id = id;
        this.nome = nome;
        this.fase = fase;
        this.dataFormacao = dataFormacao;
        this.dataEncerramento = dataEncerramento;
    }

    public void encerrarLote() {
        if (this.dataEncerramento != null) {
            throw new IllegalStateException("Este lote já foi encerrado.");
        }
        this.dataEncerramento = LocalDate.now();
    }

    public boolean isAtivo() {
        return this.dataEncerramento == null;
    }

    public UUID getId() { return id; }
    public String getNome() { return nome; }
    public FaseLote getFase() { return fase; }
    public LocalDate getDataFormacao() { return dataFormacao; }
    public LocalDate getDataEncerramento() { return dataEncerramento; }

}
