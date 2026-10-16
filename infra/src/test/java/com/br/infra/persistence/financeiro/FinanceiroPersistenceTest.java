package com.br.infra.persistence.financeiro;

import com.br.core.domain.enums.*;
import com.br.core.domain.enums.interfaces.CategoriaFinanceira;
import com.br.core.domain.model.Despesa;
import com.br.core.domain.model.LancamentoFinanceiro;
import com.br.core.domain.repository.DespesaRepository;
import com.br.core.domain.repository.LancamentoFinanceiroRepository;
import com.br.infra.persistence.repository.SpringDataDespesaRepository;
import com.br.usecase.dto.RegistrarCompraAnimalCommand;
import com.br.usecase.manejo.RegistrarCompraAnimalUseCase;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
public class FinanceiroPersistenceTest {
    private static final LocalDate REFERENCIA = LocalDate.of(2025, 12, 15);
    private final LancamentoFinanceiroRepository financeiro;
    private final DespesaRepository despesas;
    private final SpringDataDespesaRepository jpa;
    private final EntityManager em;
    private final JdbcTemplate jdbc;
    private final RegistrarCompraAnimalUseCase compra;

    @Autowired
    public FinanceiroPersistenceTest(LancamentoFinanceiroRepository financeiro, DespesaRepository despesas,
                                     SpringDataDespesaRepository jpa,
                                     EntityManager em, JdbcTemplate jdbc, RegistrarCompraAnimalUseCase compra) {
        this.financeiro = financeiro;
        this.despesas = despesas;
        this.jpa = jpa;
        this.em = em;
        this.jdbc = jdbc;
        this.compra = compra;
    }

    @ParameterizedTest
    @CsvSource({"DESPESA,PENDENTE", "RECEITA,PENDENTE", "DESPESA,EFETIVADO", "RECEITA,EFETIVADO"})
    @DisplayName("@spec:AC-700 Persistência preserva todos os dados de receita e despesa")
    void preservaDados(TipoLancamentoFinanceiro tipo, SituacaoFinanceira situacao) {
        var original = lancamento(tipo, situacao, TipoDeCusto.PASTO, UUID.randomUUID(), "1234.56");
        financeiro.salvar(original);
        descarregar();
        var recuperado = financeiro.buscarPorId(original.getId()).orElseThrow();
        assertThat(recuperado).usingRecursiveComparison().isEqualTo(original);
        assertThat(financeiro.buscarPorId(UUID.randomUUID())).isEmpty();
    }

    @Test
    @DisplayName("@spec:AC-700 Todas as categorias de receita e despesa sobrevivem à persistência")
    void preservaTodasCategorias() {
        for (CategoriaDespesa categoria : CategoriaDespesa.values()) {
            assertCategoria(TipoLancamentoFinanceiro.DESPESA, categoria);
        }
        for (CategoriaReceita categoria : CategoriaReceita.values()) {
            assertCategoria(TipoLancamentoFinanceiro.RECEITA, categoria);
        }
    }

    private void assertCategoria(TipoLancamentoFinanceiro tipo, CategoriaFinanceira categoria) {
        var original = new LancamentoFinanceiro(tipo, "Categoria " + categoria, new BigDecimal("1.00"),
                categoria, REFERENCIA, null, SituacaoFinanceira.PENDENTE, null, TipoDeCusto.GERAL, null);
        financeiro.salvar(original);
        descarregar();
        assertThat(financeiro.buscarPorId(original.getId()).orElseThrow())
                .usingRecursiveComparison().isEqualTo(original);
    }

    @Test
    @DisplayName("@spec:AC-701 Persistência exige tipo de centro de custo em todos os lançamentos")
    void exigeTipoDeCentroDeCusto() {
        assertThatThrownBy(() -> jdbc.update("""
                insert into transacao_financeira
                (id, tipo, data_transacao, valor, categoria, descricao, situacao)
                values (?, 'DESPESA', ?, 10.00, 'ENERGIA', 'Energia', 'PENDENTE')
                """, UUID.randomUUID(), REFERENCIA))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    @DisplayName("@spec:AC-713 Receitas não entram nas listas, páginas, totais ou centros de despesas")
    void isolaDespesas() {
        UUID centro = UUID.randomUUID();
        var lote = lancamento(TipoLancamentoFinanceiro.DESPESA, SituacaoFinanceira.PENDENTE,
                TipoDeCusto.LOTE, centro, "10.00");
        var animal = lancamento(TipoLancamentoFinanceiro.DESPESA, SituacaoFinanceira.EFETIVADO,
                TipoDeCusto.ANIMAL, centro, "20.00");
        var receita = lancamento(TipoLancamentoFinanceiro.RECEITA, SituacaoFinanceira.PENDENTE,
                TipoDeCusto.LOTE, centro, "100.00");
        var receitaAnimal = lancamento(TipoLancamentoFinanceiro.RECEITA, SituacaoFinanceira.EFETIVADO,
                TipoDeCusto.ANIMAL, UUID.randomUUID(), "200.00");
        financeiro.salvar(lote);
        financeiro.salvar(animal);
        financeiro.salvar(receita);
        financeiro.salvar(receitaAnimal);
        descarregar();

        assertThat(despesas.buscarPorLote(centro)).extracting(Despesa::getId).containsExactly(lote.getId());
        assertThat(despesas.buscarPorAnimal(centro)).extracting(Despesa::getId).containsExactly(animal.getId());
        assertThat(despesas.buscarPorPeriodo(REFERENCIA, REFERENCIA)).extracting(Despesa::getId)
                .containsExactlyInAnyOrder(lote.getId(), animal.getId());
        assertThat(despesas.somarDespesasNoPeriodo(REFERENCIA, REFERENCIA)).isEqualByComparingTo("30.00");
        assertThat(despesas.somarDespesasNoPeriodo(REFERENCIA.plusDays(1), REFERENCIA.plusDays(2))).isZero();
        var pagina = despesas.buscarTodosPaginado(0, 1);
        assertThat(pagina.totalElementos()).isEqualTo(2);
        assertThat(pagina.totalPaginas()).isEqualTo(2);
        assertThat(pagina.conteudo()).hasSize(1).extracting(Despesa::getId).containsAnyOf(lote.getId(), animal.getId());
        assertThat(despesas.existePorAnimalId(centro)).isTrue();
        assertThat(despesas.existePorAnimalId(receitaAnimal.getReferenciaId())).isFalse();
        assertThat(despesas.buscarPorAnimal(receitaAnimal.getReferenciaId())).isEmpty();
        assertThat(despesas.buscarPorLote(receitaAnimal.getReferenciaId())).isEmpty();
    }

    @Test
    @DisplayName("@spec:AC-713 Salvar visão Despesa preserva metadados e origem, sem sobrescrever receita")
    void preservaMetadadosNaVisaoDespesa() {
        var original = lancamento(TipoLancamentoFinanceiro.DESPESA, SituacaoFinanceira.EFETIVADO,
                TipoDeCusto.ANIMAL, UUID.randomUUID(), "25.00");
        financeiro.salvar(original);
        descarregar();
        jdbc.update("update transacao_financeira set origem_tipo = 'COMPRA_ANIMAL', origem_id = ? where id = ?",
                original.getReferenciaId(), original.getId());
        em.clear();
        var visao = despesas.buscarPorAnimal(original.getReferenciaId()).getFirst();
        assertThat(visao.getDescricao()).isEqualTo(original.getDescricao());
        despesas.salvar(visao);
        descarregar();
        assertThat(financeiro.buscarPorId(original.getId()).orElseThrow())
                .usingRecursiveComparison().isEqualTo(original);
        financeiro.salvar(original);
        descarregar();
        var entity = jpa.findById(original.getId()).orElseThrow();
        assertThat(entity.getOrigemTipo()).isEqualTo("COMPRA_ANIMAL");
        assertThat(entity.getOrigemId()).isEqualTo(original.getReferenciaId());

        var receita = lancamento(TipoLancamentoFinanceiro.RECEITA, SituacaoFinanceira.PENDENTE,
                TipoDeCusto.GERAL, null, "10.00");
        financeiro.salvar(receita);
        descarregar();
        assertThatThrownBy(() -> despesas.salvar(new Despesa(receita.getId(), "Tentativa", receita.getValor(),
                REFERENCIA, CategoriaDespesa.DIVERSOS, TipoDeCusto.GERAL, null)))
                .isInstanceOf(IllegalArgumentException.class);
        despesas.excluirPorId(receita.getId());
        assertThat(financeiro.buscarPorId(receita.getId())).isPresent();
    }

    @Test
    @DisplayName("@spec:AC-714 Compra operacional real gera uma despesa pendente com origem e UUID do animal")
    void compraContinuaPersistindoDespesa() {
        UUID animal = compra.executar(new RegistrarCompraAnimalCommand("FIN-" + UUID.randomUUID(),
                LocalDate.of(2020, 1, 1), Sexo.FEMEA, Categoria.VACA, null, REFERENCIA, new BigDecimal("3200.00")));
        descarregar();
        var registros = despesas.buscarPorAnimal(animal);
        assertThat(registros).hasSize(1);
        var despesa = registros.getFirst();
        var lancamento = financeiro.buscarPorId(despesa.getId()).orElseThrow();
        assertThat(lancamento.getSituacao()).isEqualTo(SituacaoFinanceira.PENDENTE);
        assertThat(lancamento.getDataEfetivacao()).isNull();
        assertThat(lancamento.getCategoria()).isEqualTo(CategoriaDespesa.COMPRA_ANIMAL);
        assertThat(lancamento.getDescricao()).startsWith("Compra do animal FIN-");
        assertThat(lancamento.getReferenciaId()).isEqualTo(animal);
        assertThat(lancamento.getTipoDeCusto()).isEqualTo(TipoDeCusto.ANIMAL);
        assertThat(lancamento.getValor()).isEqualByComparingTo("3200.00");
        var entity = jpa.findById(despesa.getId()).orElseThrow();
        assertThat(entity.getOrigemTipo()).isEqualTo("COMPRA_ANIMAL");
        assertThat(entity.getOrigemId()).isEqualTo(animal);
    }

    @Test
    @DisplayName("@spec:AC-714 Compra sem valor continua sem despesa")
    void compraSemValor() {
        UUID animal = compra.executar(new RegistrarCompraAnimalCommand("FIN-" + UUID.randomUUID(),
                LocalDate.of(2020, 1, 1), Sexo.FEMEA, Categoria.VACA, null, null, null));
        descarregar();
        assertThat(despesas.buscarPorAnimal(animal)).isEmpty();
    }

    private LancamentoFinanceiro lancamento(TipoLancamentoFinanceiro tipo, SituacaoFinanceira situacao,
                                           TipoDeCusto centro, UUID referenciaId, String valor) {
        return new LancamentoFinanceiro(UUID.randomUUID(), tipo, "Descrição financeira çã", new BigDecimal(valor),
                tipo == TipoLancamentoFinanceiro.DESPESA ? CategoriaDespesa.ENERGIA : CategoriaReceita.VENDA_LEITE,
                REFERENCIA, REFERENCIA.plusDays(10), situacao,
                situacao == SituacaoFinanceira.EFETIVADO ? REFERENCIA.plusMonths(1) : null, centro, referenciaId);
    }

    private void descarregar() {
        em.flush();
        em.clear();
    }
}
