package com.br.core.domain.model;

import com.br.core.domain.model.builders.LancamentoFinanceiroBuilder;
import com.br.core.domain.enums.CategoriaDespesa;
import com.br.core.domain.enums.CategoriaReceita;
import com.br.core.domain.enums.SituacaoFinanceira;
import com.br.core.domain.enums.TipoDeCusto;
import com.br.core.domain.enums.TipoLancamentoFinanceiro;
import com.br.core.domain.enums.interfaces.CategoriaFinanceira;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LancamentoFinanceiroTest {

    private static final Clock CLOCK = Clock.fixed(
            Instant.parse("2026-10-13T01:00:00Z"), ZoneId.of("America/Bahia")
    );
    private static final LocalDate HOJE = LocalDate.of(2026, 10, 12);

    @Test
    @DisplayName("@spec:AC-700 Cria despesa pendente e preserva seus dados")
    void criaDespesaPendente() {
        LancamentoFinanceiro lancamento = criar(TipoLancamentoFinanceiro.DESPESA,
                "  Compra de sal mineral  ", new BigDecimal("1500.00"), CategoriaDespesa.NUTRICAO,
                HOJE, LocalDate.of(2026, 10, 20), SituacaoFinanceira.PENDENTE, null,
                TipoDeCusto.GERAL, null);

        assertThat(lancamento.getId()).isNotNull();
        assertThat(lancamento.getTipo()).isEqualTo(TipoLancamentoFinanceiro.DESPESA);
        assertThat(lancamento.getDescricao()).isEqualTo("Compra de sal mineral");
        assertThat(lancamento.getValor()).isEqualByComparingTo("1500.00");
        assertThat(lancamento.getCategoria()).isSameAs(CategoriaDespesa.NUTRICAO);
        assertThat(lancamento.getDataReferencia()).isEqualTo(HOJE);
        assertThat(lancamento.getDataVencimento()).isEqualTo(LocalDate.of(2026, 10, 20));
        assertThat(lancamento.getSituacao()).isEqualTo(SituacaoFinanceira.PENDENTE);
        assertThat(lancamento.getDataEfetivacao()).isNull();
        assertThat(lancamento.getTipoDeCusto()).isEqualTo(TipoDeCusto.GERAL);
        assertThat(lancamento.getReferenciaId()).isNull();
    }

    @Test
    @DisplayName("@spec:AC-700 Cria receita pendente sem vencimento")
    void criaReceitaPendente() {
        LancamentoFinanceiro lancamento = criar(TipoLancamentoFinanceiro.RECEITA,
                "Venda de leite", new BigDecimal("2000.00"), CategoriaReceita.VENDA_LEITE,
                HOJE, null, SituacaoFinanceira.PENDENTE, null, TipoDeCusto.GERAL, null);

        assertThat(lancamento.getTipo()).isEqualTo(TipoLancamentoFinanceiro.RECEITA);
        assertThat(lancamento.getCategoria()).isSameAs(CategoriaReceita.VENDA_LEITE);
        assertThat(lancamento.getSituacao()).isEqualTo(SituacaoFinanceira.PENDENTE);
        assertThat(lancamento.getDataEfetivacao()).isNull();
        assertThat(lancamento.getDataVencimento()).isNull();
    }

    @Test
    @DisplayName("@spec:AC-700 Permite cadastrar despesa já paga")
    void criaDespesaPaga() {
        LancamentoFinanceiro lancamento = criar(TipoLancamentoFinanceiro.DESPESA,
                "Energia", new BigDecimal("100.00"), CategoriaDespesa.ENERGIA,
                HOJE, null, SituacaoFinanceira.EFETIVADO, HOJE, TipoDeCusto.GERAL, null);

        assertThat(lancamento.getSituacao()).isEqualTo(SituacaoFinanceira.EFETIVADO);
        assertThat(lancamento.getDataEfetivacao()).isEqualTo(HOJE);
        assertThat(lancamento.getValor()).isEqualByComparingTo("100.00");
    }

    @Test
    @DisplayName("@spec:AC-700 Permite cadastrar receita já recebida")
    void criaReceitaRecebida() {
        LocalDate data = LocalDate.of(2026, 10, 10);
        LancamentoFinanceiro lancamento = criar(TipoLancamentoFinanceiro.RECEITA,
                "Venda de leite", new BigDecimal("2000.00"), CategoriaReceita.VENDA_LEITE,
                data, null, SituacaoFinanceira.EFETIVADO, data, TipoDeCusto.GERAL, null);

        assertThat(lancamento.getSituacao()).isEqualTo(SituacaoFinanceira.EFETIVADO);
        assertThat(lancamento.getDataEfetivacao()).isEqualTo(data);
        assertThat(lancamento.getTipo()).isEqualTo(TipoLancamentoFinanceiro.RECEITA);
    }

    @Test
    @DisplayName("@spec:AC-700 Gera UUID somente para lançamento novo")
    void geraNovoId() {
        LancamentoFinanceiro primeiro = new LancamentoFinanceiro(
                TipoLancamentoFinanceiro.DESPESA, "Despesa", BigDecimal.ONE,
                CategoriaDespesa.DIVERSOS, HOJE, null, SituacaoFinanceira.PENDENTE,
                null, TipoDeCusto.GERAL, null);
        LancamentoFinanceiro segundo = new LancamentoFinanceiro(
                TipoLancamentoFinanceiro.DESPESA, "Despesa", BigDecimal.ONE,
                CategoriaDespesa.DIVERSOS, HOJE, null, SituacaoFinanceira.PENDENTE,
                null, TipoDeCusto.GERAL, null);

        assertThat(primeiro.getId()).isNotNull().isNotEqualTo(segundo.getId());
    }

    @Test
    @DisplayName("@spec:AC-700 Preserva UUID fornecido para reconstituição")
    void preservaIdInformado() {
        UUID id = UUID.randomUUID();
        LancamentoFinanceiro lancamento = new LancamentoFinanceiro(
                id, TipoLancamentoFinanceiro.DESPESA, "Despesa", BigDecimal.ONE,
                CategoriaDespesa.DIVERSOS, HOJE, null, SituacaoFinanceira.PENDENTE,
                null, TipoDeCusto.GERAL, null);

        assertThat(lancamento.getId()).isEqualTo(id);
    }

    @Test
    @DisplayName("@spec:AC-701 Rejeita UUID nulo no construtor de reconstituição")
    void rejeitaIdNulo() {
        assertThatThrownBy(() -> new LancamentoFinanceiro(
                null, TipoLancamentoFinanceiro.DESPESA, "Despesa", BigDecimal.ONE,
                CategoriaDespesa.DIVERSOS, HOJE, null, SituacaoFinanceira.PENDENTE,
                null, TipoDeCusto.GERAL, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("O ID do lançamento é obrigatório.");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "  \t\n", "\u2003"})
    @DisplayName("@spec:AC-701 Rejeita descrição ausente ou em branco")
    void rejeitaDescricaoInvalida(String descricao) {
        assertThatThrownBy(() -> despesa(descricao, BigDecimal.ONE))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("A descrição do lançamento é obrigatória.");
    }

    @ParameterizedTest
    @ValueSource(strings = {"0", "0.00", "-1", "-0.01"})
    @DisplayName("@spec:AC-701 Rejeita valores zero ou negativos")
    void rejeitaValorNaoPositivo(String valor) {
        assertThatThrownBy(() -> despesa("Despesa", new BigDecimal(valor)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("O valor do lançamento deve ser maior que zero.");
    }

    @Test
    @DisplayName("@spec:AC-701 Rejeita valor nulo")
    void rejeitaValorNulo() {
        assertThatThrownBy(() -> despesa("Despesa", null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("O valor do lançamento deve ser maior que zero.");
    }

    @ParameterizedTest
    @ValueSource(strings = {"1.001", "0.001", "1500.0001", "1.999"})
    @DisplayName("@spec:AC-701 Rejeita frações de centavo sem arredondar")
    void rejeitaFracaoDeCentavo(String valor) {
        assertThatThrownBy(() -> despesa("Despesa", new BigDecimal(valor)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("O valor do lançamento não pode ter frações de centavo.");
    }

    @ParameterizedTest
    @ValueSource(strings = {"10000000000", "9999999999.991", "1E+12"})
    @DisplayName("@spec:AC-701 Rejeita valor acima de NUMERIC(12,2)")
    void rejeitaValorAcimaDoLimite(String valor) {
        assertThatThrownBy(() -> despesa("Despesa", new BigDecimal(valor)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("O valor do lançamento não pode exceder 9.999.999.999,99.");
    }

    @ParameterizedTest
    @ValueSource(strings = {"1500", "1500.0", "1500.00", "1500.0000", "1.5E+3"})
    @DisplayName("@spec:AC-700 Aceita escalas monetariamente equivalentes")
    void aceitaEscalasEquivalentes(String valor) {
        assertThat(despesa("Despesa", new BigDecimal(valor)).getValor())
                .isEqualTo(new BigDecimal("1500.00"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"0.01", "9999999999.99", "9999999999.9900"})
    @DisplayName("@spec:AC-700 Aceita centavo mínimo e limite monetário inclusivo")
    void aceitaLimitesValidos(String valor) {
        assertThat(despesa("Despesa", new BigDecimal(valor)).getValor())
                .isEqualByComparingTo(valor);
    }

    @ParameterizedTest
    @EnumSource(CategoriaDespesa.class)
    @DisplayName("@spec:AC-701 Rejeita todas as categorias de despesa em receita")
    void rejeitaCategoriaDespesaEmReceita(CategoriaDespesa categoria) {
        assertThatThrownBy(() -> criar(TipoLancamentoFinanceiro.RECEITA, "Receita",
                BigDecimal.ONE, categoria, HOJE, null, SituacaoFinanceira.PENDENTE,
                null, TipoDeCusto.GERAL, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("A categoria deve ser compatível com o tipo do lançamento.");
    }

    @ParameterizedTest
    @EnumSource(CategoriaReceita.class)
    @DisplayName("@spec:AC-701 Rejeita todas as categorias de receita em despesa")
    void rejeitaCategoriaReceitaEmDespesa(CategoriaReceita categoria) {
        assertThatThrownBy(() -> criar(TipoLancamentoFinanceiro.DESPESA, "Despesa",
                BigDecimal.ONE, categoria, HOJE, null, SituacaoFinanceira.PENDENTE,
                null, TipoDeCusto.GERAL, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("A categoria deve ser compatível com o tipo do lançamento.");
    }

    @ParameterizedTest
    @EnumSource(CategoriaDespesa.class)
    @DisplayName("@spec:AC-700 Aceita categorias de despesa existentes e novas")
    void aceitaCategoriasDespesa(CategoriaDespesa categoria) {
        assertThat(criar(TipoLancamentoFinanceiro.DESPESA, "Despesa", BigDecimal.ONE,
                categoria, HOJE, null, SituacaoFinanceira.PENDENTE, null,
                TipoDeCusto.GERAL, null).getCategoria()).isSameAs(categoria);
    }

    @ParameterizedTest
    @EnumSource(CategoriaReceita.class)
    @DisplayName("@spec:AC-700 Aceita categorias de receita")
    void aceitaCategoriasReceita(CategoriaReceita categoria) {
        assertThat(criar(TipoLancamentoFinanceiro.RECEITA, "Receita", BigDecimal.ONE,
                categoria, HOJE, null, SituacaoFinanceira.PENDENTE, null,
                TipoDeCusto.GERAL, null).getCategoria()).isSameAs(categoria);
    }

    @Test
    @DisplayName("@spec:AC-701 Rejeita categoria nula")
    void rejeitaCategoriaNula() {
        assertThatThrownBy(() -> criar(TipoLancamentoFinanceiro.DESPESA, "Despesa",
                BigDecimal.ONE, null, HOJE, null, SituacaoFinanceira.PENDENTE,
                null, TipoDeCusto.GERAL, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("A categoria do lançamento é obrigatória.");
    }

    @Test
    @DisplayName("@spec:AC-701 Rejeita tipo financeiro nulo")
    void rejeitaTipoNulo() {
        assertThatThrownBy(() -> criar(null, "Despesa", BigDecimal.ONE,
                CategoriaDespesa.DIVERSOS, HOJE, null, SituacaoFinanceira.PENDENTE,
                null, TipoDeCusto.GERAL, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("O tipo do lançamento é obrigatório.");
    }

    @Test
    @DisplayName("@spec:AC-701 Rejeita referência temporal nula")
    void rejeitaDataReferenciaNula() {
        assertThatThrownBy(() -> criar(TipoLancamentoFinanceiro.DESPESA, "Despesa",
                BigDecimal.ONE, CategoriaDespesa.DIVERSOS, null, null,
                SituacaoFinanceira.PENDENTE, null, TipoDeCusto.GERAL, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("A data de referência é obrigatória.");
    }

    @Test
    @DisplayName("@spec:AC-700 Permite referência futura e vencimento antes ou depois")
    void aceitaDatasDePlanejamento() {
        for (LocalDate vencimento : new LocalDate[]{null, HOJE.minusDays(1), HOJE.plusYears(2)}) {
            LancamentoFinanceiro lancamento = criar(TipoLancamentoFinanceiro.DESPESA,
                    "Planejamento", BigDecimal.ONE, CategoriaDespesa.DIVERSOS,
                    HOJE.plusYears(1), vencimento, SituacaoFinanceira.PENDENTE,
                    null, TipoDeCusto.GERAL, null);
            assertThat(lancamento.getDataReferencia()).isEqualTo(HOJE.plusYears(1));
            assertThat(lancamento.getDataVencimento()).isEqualTo(vencimento);
        }
    }

    @Test
    @DisplayName("@spec:AC-701 Rejeita situação nula inclusive na reconstituição")
    void rejeitaSituacaoNula() {
        assertThatThrownBy(() -> criar(TipoLancamentoFinanceiro.DESPESA, "Despesa",
                BigDecimal.ONE, CategoriaDespesa.DIVERSOS, HOJE, null,
                null, null, TipoDeCusto.GERAL, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("A situação financeira é obrigatória.");
    }

    @Test
    @DisplayName("@spec:AC-701 Rejeita pendência com data efetiva")
    void rejeitaPendenteComDataEfetiva() {
        assertThatThrownBy(() -> criar(TipoLancamentoFinanceiro.DESPESA, "Despesa",
                BigDecimal.ONE, CategoriaDespesa.DIVERSOS, HOJE, null,
                SituacaoFinanceira.PENDENTE, HOJE, TipoDeCusto.GERAL, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Lançamento pendente não pode ter data de efetivação.");
    }

    @Test
    @DisplayName("@spec:AC-701 Rejeita efetivação sem data")
    void rejeitaEfetivadoSemData() {
        assertThatThrownBy(() -> criar(TipoLancamentoFinanceiro.RECEITA, "Receita",
                BigDecimal.ONE, CategoriaReceita.OUTRAS_RECEITAS, HOJE, null,
                SituacaoFinanceira.EFETIVADO, null, TipoDeCusto.GERAL, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Lançamento efetivado exige data de efetivação.");
    }

    @Test
    @DisplayName("@spec:AC-701 Rejeita data efetiva futura no calendário da fazenda")
    void rejeitaEfetivacaoFutura() {
        // Neste instante já é dia 13 em UTC, mas ainda é dia 12 na fazenda.
        assertThatThrownBy(() -> criar(TipoLancamentoFinanceiro.RECEITA, "Receita",
                BigDecimal.ONE, CategoriaReceita.OUTRAS_RECEITAS, HOJE, null,
                SituacaoFinanceira.EFETIVADO, HOJE.plusDays(1), TipoDeCusto.GERAL, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("A data de efetivação não pode ser futura.");
    }

    @Test
    @DisplayName("@spec:AC-700 Permite efetivação antecipada em relação à referência")
    void aceitaEfetivacaoAntecipada() {
        LancamentoFinanceiro lancamento = criar(TipoLancamentoFinanceiro.DESPESA,
                "Antecipação", BigDecimal.ONE, CategoriaDespesa.DIVERSOS,
                HOJE.plusMonths(1), HOJE.plusMonths(2), SituacaoFinanceira.EFETIVADO,
                HOJE.minusDays(1), TipoDeCusto.GERAL, null);
        assertThat(lancamento.getDataEfetivacao()).isEqualTo(HOJE.minusDays(1));
    }

    @ParameterizedTest
    @EnumSource(value = TipoDeCusto.class, names = {"ANIMAL", "LOTE", "PASTO"})
    @DisplayName("@spec:AC-701 Exige referência para centros específicos")
    void rejeitaCentroSemReferencia(TipoDeCusto tipoDeCusto) {
        assertThatThrownBy(() -> criar(TipoLancamentoFinanceiro.DESPESA, "Despesa",
                BigDecimal.ONE, CategoriaDespesa.DIVERSOS, HOJE, null,
                SituacaoFinanceira.PENDENTE, null, tipoDeCusto, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Centro de custo ANIMAL, LOTE ou PASTO exige referência.");
    }

    @ParameterizedTest
    @EnumSource(value = TipoDeCusto.class, names = {"ANIMAL", "LOTE", "PASTO"})
    @DisplayName("@spec:AC-700 Preserva referência de centro sem consultar infraestrutura")
    void aceitaCentroComReferencia(TipoDeCusto tipoDeCusto) {
        UUID referencia = UUID.randomUUID();
        LancamentoFinanceiro lancamento = criar(TipoLancamentoFinanceiro.DESPESA, "Despesa",
                BigDecimal.ONE, CategoriaDespesa.DIVERSOS, HOJE, null,
                SituacaoFinanceira.PENDENTE, null, tipoDeCusto, referencia);
        assertThat(lancamento.getTipoDeCusto()).isEqualTo(tipoDeCusto);
        assertThat(lancamento.getReferenciaId()).isEqualTo(referencia);
    }

    @Test
    @DisplayName("@spec:AC-701 Rejeita referência para centro GERAL")
    void rejeitaGeralComReferencia() {
        assertThatThrownBy(() -> criar(TipoLancamentoFinanceiro.DESPESA, "Despesa",
                BigDecimal.ONE, CategoriaDespesa.DIVERSOS, HOJE, null,
                SituacaoFinanceira.PENDENTE, null, TipoDeCusto.GERAL, UUID.randomUUID()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Centro de custo GERAL não pode ter referência.");
    }

    @Test
    @DisplayName("@spec:AC-701 Rejeita tipo de custo nulo")
    void rejeitaTipoDeCustoNulo() {
        assertThatThrownBy(() -> criar(TipoLancamentoFinanceiro.DESPESA, "Despesa",
                BigDecimal.ONE, CategoriaDespesa.DIVERSOS, HOJE, null,
                SituacaoFinanceira.PENDENTE, null, null, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("O tipo de custo é obrigatório.");
    }

    @Test
    @DisplayName("@spec:AC-700 Builder preserva campos de despesa e suas normalizações")
    void builderCriaDespesa() {
        UUID referenciaId = UUID.randomUUID();
        LancamentoFinanceiro lancamento = builderValido()
                .descricao("  Energia  ")
                .valor(new BigDecimal("200.0000"))
                .categoria(CategoriaDespesa.ENERGIA)
                .dataVencimento(HOJE.plusDays(10))
                .tipoDeCusto(TipoDeCusto.LOTE)
                .referenciaId(referenciaId)
                .build();

        assertThat(lancamento.getId()).isNotNull();
        assertThat(lancamento.getTipo()).isEqualTo(TipoLancamentoFinanceiro.DESPESA);
        assertThat(lancamento.getDescricao()).isEqualTo("Energia");
        assertThat(lancamento.getValor()).isEqualTo(new BigDecimal("200.00"));
        assertThat(lancamento.getCategoria()).isSameAs(CategoriaDespesa.ENERGIA);
        assertThat(lancamento.getDataReferencia()).isEqualTo(HOJE);
        assertThat(lancamento.getDataVencimento()).isEqualTo(HOJE.plusDays(10));
        assertThat(lancamento.getSituacao()).isEqualTo(SituacaoFinanceira.PENDENTE);
        assertThat(lancamento.getDataEfetivacao()).isNull();
        assertThat(lancamento.getTipoDeCusto()).isEqualTo(TipoDeCusto.LOTE);
        assertThat(lancamento.getReferenciaId()).isEqualTo(referenciaId);
    }

    @Test
    @DisplayName("@spec:AC-700 Builder cria receita efetivada com UUID explícito")
    void builderCriaReceitaComId() {
        UUID id = UUID.randomUUID();
        LancamentoFinanceiro lancamento = builderValido()
                .id(id)
                .tipo(TipoLancamentoFinanceiro.RECEITA)
                .categoria(CategoriaReceita.VENDA_LEITE)
                .situacao(SituacaoFinanceira.EFETIVADO)
                .dataEfetivacao(HOJE)
                .build();

        assertThat(lancamento.getId()).isEqualTo(id);
        assertThat(lancamento.getTipo()).isEqualTo(TipoLancamentoFinanceiro.RECEITA);
        assertThat(lancamento.getCategoria()).isSameAs(CategoriaReceita.VENDA_LEITE);
        assertThat(lancamento.getSituacao()).isEqualTo(SituacaoFinanceira.EFETIVADO);
        assertThat(lancamento.getDataEfetivacao()).isEqualTo(HOJE);
        assertThat(lancamento.getDataVencimento()).isNull();
        assertThat(lancamento.getReferenciaId()).isNull();
    }

    @Test
    @DisplayName("@spec:AC-701 Builder rejeita UUID explicitamente nulo no build")
    void builderRejeitaIdNulo() {
        LancamentoFinanceiroBuilder builder = builderValido().id(null);
        assertThatThrownBy(builder::build).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("O ID do lançamento é obrigatório.");
    }

    @ParameterizedTest
    @ValueSource(strings = {"tipo", "descricao", "valor", "categoria", "dataReferencia", "situacao", "tipoDeCusto"})
    @DisplayName("@spec:AC-701 Builder exige cada campo obrigatório sem defaults")
    void builderExigeCamposObrigatorios(String campoOmitido) {
        LancamentoFinanceiroBuilder builder = LancamentoFinanceiro.builder();
        if (!campoOmitido.equals("tipo")) builder.tipo(TipoLancamentoFinanceiro.DESPESA);
        if (!campoOmitido.equals("descricao")) builder.descricao("Despesa");
        if (!campoOmitido.equals("valor")) builder.valor(BigDecimal.ONE);
        if (!campoOmitido.equals("categoria")) builder.categoria(CategoriaDespesa.DIVERSOS);
        if (!campoOmitido.equals("dataReferencia")) builder.dataReferencia(HOJE);
        if (!campoOmitido.equals("situacao")) builder.situacao(SituacaoFinanceira.PENDENTE);
        if (!campoOmitido.equals("tipoDeCusto")) builder.tipoDeCusto(TipoDeCusto.GERAL);

        assertThatThrownBy(builder::build).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("@spec:AC-701 Builder delega categoria e situação às invariantes da entidade")
    void builderRejeitaCombinacoesInvalidas() {
        assertThatThrownBy(() -> builderValido().categoria(CategoriaReceita.VENDA_LEITE).build())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("A categoria deve ser compatível com o tipo do lançamento.");
        assertThatThrownBy(() -> builderValido().dataEfetivacao(HOJE).build())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Lançamento pendente não pode ter data de efetivação.");
        assertThatThrownBy(() -> builderValido().situacao(SituacaoFinanceira.EFETIVADO).build())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Lançamento efetivado exige data de efetivação.");
    }

    @Test
    @DisplayName("@spec:AC-700 Alterar Builder não modifica entidade já construída")
    void builderPreservaEntidadeConstruida() {
        LancamentoFinanceiroBuilder builder = builderValido();
        LancamentoFinanceiro original = builder.build();
        LancamentoFinanceiro alterado = builder.descricao("Outra despesa")
                .valor(new BigDecimal("200.00")).build();

        assertThat(original.getDescricao()).isEqualTo("Despesa");
        assertThat(original.getValor()).isEqualByComparingTo(BigDecimal.ONE);
        assertThat(alterado.getDescricao()).isEqualTo("Outra despesa");
        assertThat(alterado.getValor()).isEqualByComparingTo("200.00");
        assertThat(original.getId()).isNotEqualTo(alterado.getId());
    }

    @Test
    @DisplayName("@spec:AC-700 Builder reutilizado gera UUID novo quando omitido e mantém ID explícito")
    void builderReutilizadoRespeitaIdentificacao() {
        LancamentoFinanceiroBuilder builder = builderValido();
        assertThat(builder.build().getId()).isNotEqualTo(builder.build().getId());

        UUID id = UUID.randomUUID();
        builder.id(id);
        assertThat(builder.build().getId()).isEqualTo(id);
        assertThat(builder.build().getId()).isEqualTo(id);
    }

    private LancamentoFinanceiroBuilder builderValido() {
        return LancamentoFinanceiro.builder()
                .tipo(TipoLancamentoFinanceiro.DESPESA)
                .descricao("Despesa")
                .valor(BigDecimal.ONE)
                .categoria(CategoriaDespesa.DIVERSOS)
                .dataReferencia(HOJE)
                .situacao(SituacaoFinanceira.PENDENTE)
                .tipoDeCusto(TipoDeCusto.GERAL);
    }

    private LancamentoFinanceiro despesa(String descricao, BigDecimal valor) {
        return criar(TipoLancamentoFinanceiro.DESPESA, descricao, valor,
                CategoriaDespesa.DIVERSOS, HOJE, null, SituacaoFinanceira.PENDENTE,
                null, TipoDeCusto.GERAL, null);
    }

    private LancamentoFinanceiro criar(
            TipoLancamentoFinanceiro tipo, String descricao, BigDecimal valor,
            CategoriaFinanceira categoria, LocalDate referencia, LocalDate vencimento,
            SituacaoFinanceira situacao, LocalDate efetivacao, TipoDeCusto tipoDeCusto, UUID referenciaId
    ) {
        return new LancamentoFinanceiro(UUID.randomUUID(), tipo, descricao, valor, categoria,
                referencia, vencimento, situacao, efetivacao, tipoDeCusto, referenciaId, CLOCK);
    }
}
