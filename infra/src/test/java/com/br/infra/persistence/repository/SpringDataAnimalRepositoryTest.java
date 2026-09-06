package com.br.infra.persistence.repository;

import com.br.infra.persistence.entity.AnimalEntity;
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
class SpringDataAnimalRepositoryTest {

    @Autowired
    private SpringDataAnimalRepository animalRepository;

    @Test
    @DisplayName("Deve salvar e buscar um animal ativo pelo brinco RGD com sucesso no PostgreSQL")
    void deveSalvarEBuscarAnimalPorBrinco() {
        UUID animalId = UUID.randomUUID();
        AnimalEntity animal = new AnimalEntity(
                animalId, "BRINCO-999", null,
                LocalDate.now().minusMonths(8), "MACHO",
                "BEZERRO", "ATIVO", null
        );

        animalRepository.save(animal);

        var encontrado = animalRepository.findByBrincoRgd("BRINCO-999");

        assertThat(encontrado).isPresent();
        assertThat(encontrado.get().getCategoriaAtual()).isEqualTo("BEZERRO");
        assertThat(encontrado.get().getStatus()).isEqualTo("ATIVO");
    }
}