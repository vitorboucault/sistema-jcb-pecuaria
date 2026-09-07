package com.br.infra.persistence.adapter;

import com.br.core.domain.enums.ModalidadeVenda;
import com.br.core.domain.model.VendaAnimal;
import com.br.core.domain.repository.VendaAnimalRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class VendaAnimalRepositoryImplTest {

    @Autowired
    private VendaAnimalRepository vendaRepository;

    @Test
    void vendaExcluidaDeixaDeComporReceitaDoPeriodo() {
        UUID vendaId = UUID.randomUUID();
        VendaAnimal venda = new VendaAnimal(vendaId, UUID.randomUUID(), LocalDate.now(),
                ModalidadeVenda.POR_CABECA, 500.0, null, new BigDecimal("1234.50"));
        LocalDate inicio = LocalDate.now().minusDays(1);
        LocalDate fim = LocalDate.now().plusDays(1);

        vendaRepository.salvar(venda);

        assertThat(vendaRepository.somarReceitasNoPeriodo(inicio, fim))
                .isEqualByComparingTo("1234.50");

        vendaRepository.excluirPorId(vendaId);

        assertThat(vendaRepository.somarReceitasNoPeriodo(inicio, fim))
                .isEqualByComparingTo("0.00");
    }
}
