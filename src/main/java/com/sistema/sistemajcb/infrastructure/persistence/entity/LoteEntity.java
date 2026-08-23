package com.sistema.sistemajcb.infrastructure.persistence.entity;

import com.sistema.sistemajcb.domain.enums.FaseLote;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "lote")
public class LoteEntity {

    @Id
    private UUID id;

    @Column(name = "nome", unique = true, nullable = false, length = 50)
    private String nome;

    @Enumerated(EnumType.STRING)
    @Column(name = "fase_lote", nullable = false, length = 50)
    private FaseLote fase;

    @Column(name = "data_formacao", nullable = false)
    private LocalDate dataFormacao;

    @Column(name = "data_encerramento", nullable = false)
    private LocalDate dataEncerramento;

    protected LoteEntity() {};

    public LoteEntity(UUID id, String nome, FaseLote fase, LocalDate dataFormacao, LocalDate dataEncerramento) {
        this.id = id;
        this.nome = nome;
        this.fase = fase;
        this.dataFormacao = dataFormacao;
        this.dataEncerramento = dataEncerramento;
    }

    public UUID getId() {return id;}
    public String getNome() {return nome;}
    public FaseLote getFase() {return fase;}
    public LocalDate getDataFormacao() {return dataFormacao;}
    public LocalDate getDataEncerramento() {return dataEncerramento;}
}
