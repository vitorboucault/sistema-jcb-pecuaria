package com.br.infra.persistence.repository;

import com.br.infra.persistence.entity.AnimalEntity;
import com.br.infra.persistence.entity.PesagemEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class SpringDataPesagemRepositoryTest {

    @Autowired
    private SpringDataPesagemRepository pesagemRepository;

    @Autowired
    private SpringDataAnimalRepository animalRepository; // Injetamos o repositório de animal

    @Test
    @DisplayName("Deve registrar e recuperar a pesagem de um animal com sucesso no PostgreSQL")
    void deveSalvarEBuscarPesagem() {
        UUID animalId = UUID.randomUUID();

        AnimalEntity animal = new AnimalEntity(
                animalId, "BRINCO-TESTE-01", null,
                LocalDate.now().minusMonths(12), "MACHO",
                "BOI", "ATIVO", null
        );
        animalRepository.save(animal);

        UUID pesagemId = UUID.randomUUID();
        PesagemEntity pesagem = new PesagemEntity(
                pesagemId, animalId, LocalDate.now(), 450.5
        );

        pesagemRepository.save(pesagem);
        pesagemRepository.flush();

        var encontrada = pesagemRepository.findById(pesagemId);

        assertThat(encontrada).isPresent();
        assertThat(encontrada.get().getPeso()).isEqualTo(450.5);
        assertThat(encontrada.get().getAnimalId()).isEqualTo(animalId);
    }
}