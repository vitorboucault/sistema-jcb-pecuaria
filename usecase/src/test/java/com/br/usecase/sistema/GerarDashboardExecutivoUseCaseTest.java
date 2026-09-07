package com.br.usecase.sistema;

import com.br.core.domain.repository.AnimalRepository;
import com.br.usecase.financeiro.CalcularCustoArrobaUseCase;
import com.br.usecase.financeiro.CalcularDesembolsoCabecaMesUseCase;
import com.br.usecase.financeiro.CalcularGmdGlobalUseCase;
import com.br.usecase.financeiro.CalcularMargemBrutaHectareUseCase;
import com.br.usecase.nutricao.CalcularConversaoAlimentarUseCase;
import com.br.usecase.zootecnico.CalcularPontoEquilibrioUseCase;
import com.br.usecase.zootecnico.CalcularTaxaDesmameUseCase;
import com.br.usecase.zootecnico.CalcularTaxaLotacaoUseCase;
import com.br.usecase.zootecnico.CalcularTaxaPrenhezUseCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GerarDashboardExecutivoUseCaseTest {

    @Mock
    private CalcularMargemBrutaHectareUseCase margemBrutaUseCase;

    @Mock
    private CalcularPontoEquilibrioUseCase pontoEquilibrioUseCase;

    @Mock
    private CalcularDesembolsoCabecaMesUseCase desembolsoUseCase;

    @Mock
    private CalcularCustoArrobaUseCase custoArrobaUseCase;

    @Mock
    private CalcularGmdGlobalUseCase gmdGlobalUseCase;

    @Mock
    private CalcularConversaoAlimentarUseCase conversaoAlimentarUseCase;

    @Mock
    private CalcularTaxaPrenhezUseCase taxaPrenhezUseCase;

    @Mock
    private CalcularTaxaDesmameUseCase taxaDesmameUseCase;

    @Mock
    private CalcularTaxaLotacaoUseCase taxaLotacaoUseCase;

    @Mock
    private AnimalRepository animalRepository;

    @InjectMocks
    private GerarDashboardExecutivoUseCase useCase;

    @Test
    @DisplayName("Total do rebanho ativo vem do repository para o DTO")
    void totalRebanhoAtivoVemDoRepositoryParaODto() {
        LocalDate inicioSafra = LocalDate.now().minusMonths(6);
        LocalDate fimSafra = LocalDate.now();
        BigDecimal precoArrobaHoje = new BigDecimal("310.00");

        when(animalRepository.buscarAnimaisElegiveisParaEvolucao()).thenReturn(List.of());
        when(animalRepository.contarAnimaisAtivos()).thenReturn(127L);
        when(margemBrutaUseCase.executar(inicioSafra, fimSafra)).thenReturn(BigDecimal.ONE);
        when(pontoEquilibrioUseCase.executar(inicioSafra, fimSafra, precoArrobaHoje)).thenReturn(BigDecimal.TEN);
        when(desembolsoUseCase.executar(inicioSafra, fimSafra)).thenReturn(BigDecimal.ZERO);
        when(custoArrobaUseCase.executar(inicioSafra, fimSafra)).thenReturn(BigDecimal.ZERO);
        when(gmdGlobalUseCase.executar(inicioSafra, fimSafra, List.of())).thenReturn(BigDecimal.ZERO);

        DashboardExecutivoDTO resultado = useCase.executar(
                inicioSafra,
                fimSafra,
                precoArrobaHoje,
                null,
                null,
                null
        );

        assertThat(resultado.getTotalRebanho()).isEqualTo(127L);
        verify(animalRepository).contarAnimaisAtivos();
    }
}
