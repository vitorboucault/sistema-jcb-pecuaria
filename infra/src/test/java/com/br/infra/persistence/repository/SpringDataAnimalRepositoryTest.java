package com.br.infra.persistence.repository;

import com.br.infra.persistence.entity.AnimalEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

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

    @Test
    @DisplayName("Deve contar animais ativos por categoria ignorando mortos")
    void deveContarAnimaisAtivosPorCategoriaIgnorandoMortos() {
        animalRepository.save(new AnimalEntity(UUID.randomUUID(), "ATIVO-BEZERRO-1", null,
                LocalDate.now().minusMonths(8), "MACHO", "BEZERRO", "ATIVO", null));
        animalRepository.save(new AnimalEntity(UUID.randomUUID(), "ATIVO-BEZERRO-2", null,
                LocalDate.now().minusMonths(8), "MACHO", "BEZERRO", "ATIVO", null));
        animalRepository.save(new AnimalEntity(UUID.randomUUID(), "ATIVO-VACA-1", null,
                LocalDate.now().minusYears(3), "FEMEA", "VACA", "ATIVO", null));
        animalRepository.save(new AnimalEntity(UUID.randomUUID(), "MORTO-BEZERRO-1", null,
                LocalDate.now().minusMonths(8), "MACHO", "BEZERRO", "MORTO", null, LocalDate.now()));

        Map<String, Long> contagem = animalRepository.contarAtivosPorCategoria()
                .stream()
                .collect(Collectors.toMap(
                        SpringDataAnimalRepository.ContagemPorCategoriaProjection::getCategoria,
                        SpringDataAnimalRepository.ContagemPorCategoriaProjection::getTotal
                ));

        assertThat(contagem).containsEntry("BEZERRO", 2L);
        assertThat(contagem).containsEntry("VACA", 1L);
        assertThat(contagem).doesNotContainKey("MORTO");
    }
}
