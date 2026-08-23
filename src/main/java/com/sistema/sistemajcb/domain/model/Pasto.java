package com.sistema.sistemajcb.domain.model;

import com.sistema.sistemajcb.domain.enums.StatusPasto;

import java.util.UUID;

public class Pasto {

    private final UUID id;
    private String nome;
    private double areaHectares;
    private double capacidadeSuporteUa; // Unidades Animais suportadas
    private StatusPasto statusAtual;

    public Pasto(String nome, double areaHectares, double capacidadeSuporteUa) {
        if (areaHectares <= 0) {
            throw new IllegalArgumentException("A área do pasto deve ser maior que zero.");
        }
        this.id = UUID.randomUUID();
        this.nome = nome;
        this.areaHectares = areaHectares;
        this.capacidadeSuporteUa = capacidadeSuporteUa;
        this.statusAtual = StatusPasto.DESCANSO; //
    }

    public Pasto(UUID id, String nome, double areaHectares, double capacidadeSuporteUa, StatusPasto statusAtual) {
        this.id = id;
        this.nome = nome;
        this.areaHectares = areaHectares;
        this.capacidadeSuporteUa = capacidadeSuporteUa;
        this.statusAtual = statusAtual;
    }

    public void registrarEntradaGado() {
        if (this.statusAtual == StatusPasto.MANUTENCAO) {
            throw new IllegalStateException("Não é possível colocar gado em um pasto em manutenção/reforma.");
        }
        this.statusAtual = StatusPasto.PASTEJO;
    }

    public void vedarPasto() {
        this.statusAtual = StatusPasto.DESCANSO;
    }

    public void iniciarManutencao() {
        this.statusAtual = StatusPasto.MANUTENCAO;
    }

    public UUID getId() { return id; }
    public String getNome() { return nome; }
    public double getAreaHectares() { return areaHectares; }
    public double getCapacidadeSuporteUa() { return capacidadeSuporteUa; }
    public StatusPasto getStatusAtual() { return statusAtual; }

}
