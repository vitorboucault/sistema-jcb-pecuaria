package com.br.core.domain.model;

import com.br.core.domain.enums.Categoria;
import com.br.core.domain.enums.Sexo;
import com.br.core.domain.enums.Status;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

public class Animal {

    private final UUID id;
    private String brincoRgd;
    private LocalDate dataNascimento;
    private Sexo sexo;
    private Categoria categoriaAtual;
    private Status status;
    private UUID maeId;
    private UUID loteId;
    private LocalDate dataMorte;

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
        this(id, brincoRgd, dataNascimento, sexo, categoria, status, maeId, loteId, null);
    }

    public Animal(UUID id, String brincoRgd, LocalDate dataNascimento, Sexo sexo, Categoria categoria, Status status, UUID maeId, UUID loteId, LocalDate dataMorte) {
        this.id = id;
        this.brincoRgd = brincoRgd;
        this.dataNascimento = dataNascimento;
        this.sexo = sexo;
        this.categoriaAtual = categoria;
        this.status = status;
        this.maeId = maeId;
        this.loteId = loteId;
        this.dataMorte = dataMorte;
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

    public void registrarMorte(LocalDate dataMorte) {
        if (dataMorte == null) {
            throw new IllegalArgumentException("A data da morte é obrigatória.");
        }
        if (dataMorte.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("A data da morte nao pode ser futura.");
        }
        if (this.status != Status.ATIVO) {
            throw new IllegalStateException("Somente animais ativos podem receber baixa por morte.");
        }
        this.status = Status.MORTO;
        this.dataMorte = dataMorte;
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
        if (this.status != Status.ATIVO) {
            throw new IllegalStateException("Somente animais ativos podem ser vendidos.");
        }
        this.status = Status.VENDIDO;
        this.loteId = null;
    }

    public void reverterMorte() {
        if (this.status != Status.MORTO) {
            throw new IllegalStateException("Somente animais mortos podem ter a morte revertida.");
        }
        this.status = Status.ATIVO;
        this.dataMorte = null;
    }

    public void reverterVenda() {
        if (this.status != Status.VENDIDO) {
            throw new IllegalStateException("Somente animais vendidos podem ter a venda revertida.");
        }
        this.status = Status.ATIVO;
        this.loteId = null;
    }

    public void atualizarDadosCadastrais(String brincoRgd, LocalDate dataNascimento, Sexo sexo, Categoria categoria) {
        if (brincoRgd == null || brincoRgd.isBlank()) {
            throw new IllegalArgumentException("O brinco/RGD é obrigatório.");
        }
        if (dataNascimento == null) {
            throw new IllegalArgumentException("A data de nascimento é obrigatória.");
        }
        if (dataNascimento.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("O bezerro nao pode nascer no futuro.");
        }
        if (sexo == null) {
            throw new IllegalArgumentException("O sexo é obrigatório.");
        }
        if (categoria == null) {
            throw new IllegalArgumentException("A categoria é obrigatória.");
        }

        this.brincoRgd = brincoRgd;
        this.dataNascimento = dataNascimento;
        this.sexo = sexo;
        this.categoriaAtual = categoria;
    }

    public UUID getId() { return id; }
    public String getBrincoRgd() { return brincoRgd; }
    public LocalDate getDataNascimento() { return dataNascimento; }
    public Sexo getSexo() { return sexo; }
    public Categoria getCategoriaAtual() { return categoriaAtual; }
    public Status getStatus() { return status; }
    public UUID getMaeId() { return maeId; }
    public UUID getLoteId() { return loteId; }
    public LocalDate getDataMorte() { return dataMorte; }

    public void setCategoriaAtual(Categoria categoriaAtual) {
        this.categoriaAtual = categoriaAtual;
    }

    public void setMaeId(UUID maeId) {
        this.maeId = maeId;
    }
}
