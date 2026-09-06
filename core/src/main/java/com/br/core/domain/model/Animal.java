package com.br.core.domain.model;

import com.br.core.domain.enums.Categoria;
import com.br.core.domain.enums.Sexo;
import com.br.core.domain.enums.Status;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

public class Animal {

    private final UUID id;
    private final String brincoRgd;
    private final LocalDate dataNascimento;
    private final Sexo sexo;
    private Categoria categoriaAtual;
    private Status status;
    private UUID maeId;
    private UUID loteId;

    public Animal(String brincoRgd, LocalDate dataNascimento, Sexo sexo, UUID maeId, UUID loteInicial) {
        if (dataNascimento.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("O bezerro nao pode nascer no futuro.");
        }
        this.id = UUID.randomUUID();
        this.brincoRgd = brincoRgd;
        this.dataNascimento = dataNascimento;
        this.sexo = sexo;
        this.maeId = maeId;
        this.status = Status.ATIVO;
        this.categoriaAtual = (sexo == Sexo.MACHO) ? Categoria.BEZERRO : Categoria.BEZERRA;
        this.loteId = loteInicial;
    }

    public Animal(UUID id, String brincoRgd, LocalDate dataNascimento, Sexo sexo, Categoria categoria, Status status, UUID maeId, UUID loteId) {
        this.id = id;
        this.brincoRgd = brincoRgd;
        this.dataNascimento = dataNascimento;
        this.sexo = sexo;
        this.categoriaAtual = categoria;
        this.status = status;
        this.maeId = maeId;
        this.loteId = loteId;
    }

    public void registrarDesmame() {
        long mesesIdade = ChronoUnit.MONTHS.between(this.dataNascimento, LocalDate.now());
        if (mesesIdade < 8) {
            throw new IllegalStateException("Animal muito jovem para desmame precoce (Minimo 8 meses).");
        }
        if (this.categoriaAtual != Categoria.BEZERRO && this.categoriaAtual != Categoria.BEZERRA) {
            throw new IllegalStateException("Apenas bezerros(as) podem ser desmamados.");
        }
        this.categoriaAtual = (this.sexo == Sexo.MACHO) ? Categoria.GARROTE : Categoria.NOVILHA;
    }

    public void registrarMorte() {
        this.status = Status.MORTO;
        this.loteId = null;
    }

    public void transferirParaLote(UUID novoLoteId) {
        if (this.status != Status.ATIVO) {
            throw new IllegalStateException("Apenas animais ativos podem ser alocados em lotes.");
        }
        this.loteId = novoLoteId;
    }

    public boolean avaliarEvolucaoPorIdade() {
        if (this.status != Status.ATIVO) {
            return false;
        }
        long mesesIdade = ChronoUnit.MONTHS.between(this.dataNascimento, LocalDate.now());
        boolean evoluiu = false;

        if (mesesIdade >= 8 && (this.categoriaAtual == Categoria.BEZERRO || this.categoriaAtual == Categoria.BEZERRA)) {
            this.categoriaAtual = (this.sexo == Sexo.MACHO) ? Categoria.GARROTE : Categoria.NOVILHA;
            evoluiu = true;
        } else if (mesesIdade >= 24 && this.categoriaAtual == Categoria.GARROTE) {
            this.categoriaAtual = Categoria.BOI;
            evoluiu = true;
        }
        return evoluiu;
    }

    public void registrarVenda() {
        this.status = Status.VENDIDO;
        this.loteId = null;
    }

    public UUID getId() { return id; }
    public String getBrincoRgd() { return brincoRgd; }
    public LocalDate getDataNascimento() { return dataNascimento; }
    public Sexo getSexo() { return sexo; }
    public Categoria getCategoriaAtual() { return categoriaAtual; }
    public Status getStatus() { return status; }
    public UUID getMaeId() { return maeId; }
    public UUID getLoteId() { return loteId; }

    public void setCategoriaAtual(Categoria categoriaAtual) {
        this.categoriaAtual = categoriaAtual;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void setMaeId(UUID maeId) {
        this.maeId = maeId;
    }

    public void setLoteId(UUID loteId) {
        this.loteId = loteId;
    }
}
