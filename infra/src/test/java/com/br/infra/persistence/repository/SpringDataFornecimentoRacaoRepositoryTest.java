package com.br.infra.persistence.repository;

import com.br.core.domain.enums.FaseLote;
import com.br.infra.persistence.entity.FornecimentoRacaoEntity;
import com.br.infra.persistence.entity.LoteEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@TestPropertySource(properties = {
        "spring.flyway.enabled=false",
        "spring.jpa.hibernate.ddl-auto=update"
})
@Transactional
class SpringDataFornecimentoRacaoRepositoryTest {

    @Autowired
    private SpringDataFornecimentoRacaoRepository fornecimentoRepository;

    @Autowired
    private SpringDataLoteRepository loteRepository;

    @Test
    @DisplayName("Deve persistir fornecimento de ração e somar consumo total de Matéria Seca do lote com precisão")
    void deveSomarConsumoMateriaSecaCorretamente() {
        // 1. Criar lote de engorda para respeitar a foreign key
        UUID loteId = UUID.randomUUID();
        LoteEntity lote = new LoteEntity(
                loteId, "LOTE-CONFINAMENTO-01", FaseLote.ENGORDA, LocalDate.now().minusDays(30), null
        );
        loteRepository.save(lote);

        // 2. Lançar 2 dias de cocho com dietas diferentes
        // Dia 1: 1000 kg de silagem a 35% de MS = 350 kg de MS
        FornecimentoRacaoEntity trato1 = new FornecimentoRacaoEntity(
                UUID.randomUUID(), loteId, LocalDate.now().minusDays(2), 1000.0, 35.0
        );
        // Dia 2: 500 kg de concentrado/grão a 88% de MS = 440 kg de MS
        FornecimentoRacaoEntity trato2 = new FornecimentoRacaoEntity(
                UUID.randomUUID(), loteId, LocalDate.now().minusDays(1), 500.0, 88.0
        );

        fornecimentoRepository.save(trato1);
        fornecimentoRepository.save(trato2);
        fornecimentoRepository.flush();

        // 3. Consultar consumo total de MS no período
        LocalDate inicio = LocalDate.now().minusDays(5);
        LocalDate fim = LocalDate.now();
        Double totalMs = fornecimentoRepository.somarConsumoMateriaSecaPorLoteNoPeriodo(loteId, inicio, fim);

        // Total esperado: 350.0 + 440.0 = 790.0 kg MS
        assertThat(totalMs).isEqualTo(790.0);
    }
}