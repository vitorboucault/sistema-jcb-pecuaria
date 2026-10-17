package com.br.usecase.financeiro;

import com.br.core.domain.enums.CategoriaDespesa;
import com.br.core.domain.enums.CategoriaReceita;
import com.br.core.domain.enums.SituacaoFinanceira;
import com.br.core.domain.enums.TipoDeCusto;
import com.br.core.domain.enums.TipoLancamentoFinanceiro;
import com.br.core.domain.enums.interfaces.CategoriaFinanceira;
import com.br.core.domain.model.Animal;
import com.br.core.domain.model.LancamentoFinanceiro;
import com.br.core.domain.model.Lote;
import com.br.core.domain.model.Pasto;
import com.br.core.domain.repository.AnimalRepository;
import com.br.core.domain.repository.LancamentoFinanceiroRepository;
import com.br.core.domain.repository.LoteRepository;
import com.br.core.domain.repository.PastoRepository;
import com.br.usecase.dto.RegistrarLancamentoFinanceiroCommand;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegistrarLancamentoFinanceiroUseCaseTest {
    private static final LocalDate DATA = LocalDate.of(2025, 1, 10);
    @Mock private LancamentoFinanceiroRepository repository;
    @Mock private AnimalRepository animais;
    @Mock private LoteRepository lotes;
    @Mock private PastoRepository pastos;
    @InjectMocks private RegistrarLancamentoFinanceiroUseCase useCase;

    @ParameterizedTest
    @CsvSource({"DESPESA,PENDENTE", "RECEITA,PENDENTE", "DESPESA,EFETIVADO", "RECEITA,EFETIVADO"})
    @DisplayName("@spec:AC-700 Cadastro constrói pelo domínio, persiste e retorna receita/despesa")
    void cadastra(TipoLancamentoFinanceiro tipo, SituacaoFinanceira situacao) {
        CategoriaFinanceira categoria = tipo == TipoLancamentoFinanceiro.DESPESA
                ? CategoriaDespesa.ENERGIA : CategoriaReceita.VENDA_LEITE;
        var command = new RegistrarLancamentoFinanceiroCommand(tipo, "  Operação financeira  ",
                new BigDecimal("150.50"), categoria, DATA, DATA.plusDays(5), situacao,
                situacao == SituacaoFinanceira.EFETIVADO ? DATA.plusDays(1) : null, TipoDeCusto.GERAL, null);

        LancamentoFinanceiro resultado = useCase.executar(command);

        verify(repository).salvar(resultado);
        verifyNoMoreInteractions(repository);
        verifyNoInteractions(animais, lotes, pastos);
        assertThat(resultado.getId()).isNotNull();
        assertThat(resultado.getTipo()).isEqualTo(tipo);
        assertThat(resultado.getDescricao()).isEqualTo("Operação financeira");
        assertThat(resultado.getCategoria()).isSameAs(categoria);
        assertThat(resultado.getValor()).isEqualByComparingTo("150.50");
        assertThat(resultado.getDataReferencia()).isEqualTo(DATA);
        assertThat(resultado.getDataVencimento()).isEqualTo(DATA.plusDays(5));
        assertThat(resultado.getSituacao()).isEqualTo(situacao);
        assertThat(resultado.getDataEfetivacao()).isEqualTo(command.dataEfetivacao());
    }

    @ParameterizedTest
    @EnumSource(value = TipoDeCusto.class, names = {"ANIMAL", "LOTE", "PASTO"})
    @DisplayName("@spec:AC-701 Centro existente é consultado na porta correspondente antes de salvar")
    void validaExistenciaDoCentro(TipoDeCusto centro) {
        UUID id = UUID.randomUUID();
        switch (centro) {
            case ANIMAL -> when(animais.buscarPorId(id)).thenReturn(Optional.of(mock(Animal.class)));
            case LOTE -> when(lotes.buscarPorId(id)).thenReturn(Optional.of(mock(Lote.class)));
            case PASTO -> when(pastos.buscarPorId(id)).thenReturn(Optional.of(mock(Pasto.class)));
            default -> throw new AssertionError(centro);
        }
        var resultado = useCase.executar(command(new BigDecimal("10.00"), CategoriaDespesa.ENERGIA,
                DATA, SituacaoFinanceira.PENDENTE, null, centro, id));
        switch (centro) {
            case ANIMAL -> {
                var ordem = inOrder(animais, repository);
                ordem.verify(animais).buscarPorId(id);
                ordem.verify(repository).salvar(resultado);
                verifyNoInteractions(lotes, pastos);
            }
            case LOTE -> {
                var ordem = inOrder(lotes, repository);
                ordem.verify(lotes).buscarPorId(id);
                ordem.verify(repository).salvar(resultado);
                verifyNoInteractions(animais, pastos);
            }
            case PASTO -> {
                var ordem = inOrder(pastos, repository);
                ordem.verify(pastos).buscarPorId(id);
                ordem.verify(repository).salvar(resultado);
                verifyNoInteractions(animais, lotes);
            }
            default -> throw new AssertionError(centro);
        }
        assertThat(resultado.getReferenciaId()).isEqualTo(id);
        assertThat(resultado.getTipoDeCusto()).isEqualTo(centro);
    }

    @ParameterizedTest
    @EnumSource(value = TipoDeCusto.class, names = {"ANIMAL", "LOTE", "PASTO"})
    @DisplayName("@spec:AC-701 Centro inexistente rejeita cadastro sem persistência")
    void rejeitaCentroInexistente(TipoDeCusto centro) {
        UUID id = UUID.randomUUID();
        assertThatThrownBy(() -> useCase.executar(command(new BigDecimal("10.00"), CategoriaDespesa.ENERGIA,
                DATA, SituacaoFinanceira.PENDENTE, null, centro, id)))
                .isInstanceOf(IllegalArgumentException.class).hasMessageContaining("não encontrado");
        verifyNoInteractions(repository);
        switch (centro) {
            case ANIMAL -> verify(animais).buscarPorId(id);
            case LOTE -> verify(lotes).buscarPorId(id);
            case PASTO -> verify(pastos).buscarPorId(id);
            default -> throw new AssertionError(centro);
        }
    }

    @ParameterizedTest
    @MethodSource("invalidos")
    @DisplayName("@spec:AC-701 Invariantes do domínio rejeitam cadastro antes de acessar portas")
    void rejeitaInvariantes(RegistrarLancamentoFinanceiroCommand command) {
        assertThatThrownBy(() -> useCase.executar(command)).isInstanceOf(IllegalArgumentException.class);
        verifyNoInteractions(repository, animais, lotes, pastos);
    }

    static Stream<RegistrarLancamentoFinanceiroCommand> invalidos() {
        Stream<RegistrarLancamentoFinanceiroCommand> valores = Stream.of("0", "-1", "10000000000.00", "1.001")
                .map(valor -> command(new BigDecimal(valor), CategoriaDespesa.ENERGIA, DATA,
                        SituacaoFinanceira.PENDENTE, null, TipoDeCusto.GERAL, null));
        return Stream.concat(valores, Stream.of(
                command(null, CategoriaDespesa.ENERGIA, DATA, SituacaoFinanceira.PENDENTE, null, TipoDeCusto.GERAL, null),
                command(BigDecimal.TEN, CategoriaReceita.VENDA_LEITE, DATA, SituacaoFinanceira.PENDENTE, null, TipoDeCusto.GERAL, null),
                command(BigDecimal.TEN, CategoriaDespesa.ENERGIA, null, SituacaoFinanceira.PENDENTE, null, TipoDeCusto.GERAL, null),
                command(BigDecimal.TEN, CategoriaDespesa.ENERGIA, DATA, SituacaoFinanceira.EFETIVADO, null, TipoDeCusto.GERAL, null),
                command(BigDecimal.TEN, CategoriaDespesa.ENERGIA, DATA, SituacaoFinanceira.PENDENTE, DATA, TipoDeCusto.GERAL, null),
                command(BigDecimal.TEN, CategoriaDespesa.ENERGIA, DATA, SituacaoFinanceira.EFETIVADO,
                        LocalDate.now(ZoneId.of("America/Bahia")).plusDays(2), TipoDeCusto.GERAL, null),
                command(BigDecimal.TEN, CategoriaDespesa.ENERGIA, DATA, SituacaoFinanceira.PENDENTE, null, TipoDeCusto.GERAL, UUID.randomUUID()),
                command(BigDecimal.TEN, CategoriaDespesa.ENERGIA, DATA, SituacaoFinanceira.PENDENTE, null, TipoDeCusto.ANIMAL, null)
        ));
    }

    private static RegistrarLancamentoFinanceiroCommand command(BigDecimal valor, CategoriaFinanceira categoria,
            LocalDate referencia, SituacaoFinanceira situacao, LocalDate efetivacao, TipoDeCusto centro, UUID id) {
        return new RegistrarLancamentoFinanceiroCommand(TipoLancamentoFinanceiro.DESPESA, "Energia", valor,
                categoria, referencia, null, situacao, efetivacao, centro, id);
    }
}
