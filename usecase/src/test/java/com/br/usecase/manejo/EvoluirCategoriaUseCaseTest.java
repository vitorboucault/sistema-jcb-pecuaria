package com.br.usecase.manejo;

import com.br.core.domain.enums.Categoria;
import com.br.core.domain.enums.Sexo;
import com.br.core.domain.enums.Status;
import com.br.core.domain.model.Animal;
import com.br.core.domain.repository.AnimalRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EvoluirCategoriaUseCaseTest {

    @Mock
    private AnimalRepository animalRepository;

    @InjectMocks
    private EvoluirCategoriaUseCase useCase;

    @Test
    @DisplayName("Deve evoluir bezerros para garrote/novilha aos 8 meses e garrote para boi aos 24 meses")
    void deveEvoluirCategoriasCorretamente() {
        // Bezerro macho com 9 meses -> deve virar GARROTE
        Animal bezerroMacho = new Animal(UUID.randomUUID(), "BEZ-M", LocalDate.now().minusMonths(9),
                Sexo.MACHO, Categoria.BEZERRO, Status.ATIVO, null, UUID.randomUUID());

        // Bezerra fêmea com 10 meses -> deve virar NOVILHA
        Animal bezerraFemea = new Animal(UUID.randomUUID(), "BEZ-F", LocalDate.now().minusMonths(10),
                Sexo.FEMEA, Categoria.BEZERRA, Status.ATIVO, null, UUID.randomUUID());

        // Garrote macho com 25 meses -> deve virar BOI
        Animal garrote = new Animal(UUID.randomUUID(), "GAR-01", LocalDate.now().minusMonths(25),
                Sexo.MACHO, Categoria.GARROTE, Status.ATIVO, null, UUID.randomUUID());

        // Bezerro jovem com 4 meses -> NÃO deve evoluir
        Animal bezerroJovem = new Animal(UUID.randomUUID(), "BEZ-NOVO", LocalDate.now().minusMonths(4),
                Sexo.MACHO, Categoria.BEZERRO, Status.ATIVO, null, UUID.randomUUID());

        when(animalRepository.buscarAnimaisElegiveisParaEvolucao())
                .thenReturn(List.of(bezerroMacho, bezerraFemea, garrote, bezerroJovem));

        useCase.executar();

        assertThat(bezerroMacho.getCategoriaAtual()).isEqualTo(Categoria.GARROTE);
        assertThat(bezerraFemea.getCategoriaAtual()).isEqualTo(Categoria.NOVILHA);
        assertThat(garrote.getCategoriaAtual()).isEqualTo(Categoria.BOI);
        assertThat(bezerroJovem.getCategoriaAtual()).isEqualTo(Categoria.BEZERRO);

        // Apenas 3 animais evoluíram e devem ser persistidos
        verify(animalRepository, times(3)).salvar(any(Animal.class));
    }
}