package com.br.usecase.manejo;

import com.br.core.domain.enums.Categoria;
import com.br.core.domain.repository.AnimalRepository;
import com.br.usecase.dto.ResumoRebanhoDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ObterResumoRebanhoUseCaseTest {

    @Mock
    private AnimalRepository animalRepository;

    @InjectMocks
    private ObterResumoRebanhoUseCase useCase;

    @Test
    @DisplayName("Total considera apenas animais ativos")
    void totalConsideraApenasAnimaisAtivos() {
        when(animalRepository.contarAtivosPorCategoria()).thenReturn(Map.of(
                Categoria.BEZERRO, 2L,
                Categoria.VACA, 3L
        ));

        ResumoRebanhoDTO resumo = useCase.executar();

        assertThat(resumo.total()).isEqualTo(5L);
    }

    @Test
    @DisplayName("Mortos nao entram no resumo")
    void mortosNaoEntramNoResumo() {
        when(animalRepository.contarAtivosPorCategoria()).thenReturn(Map.of(
                Categoria.BOI, 4L
        ));

        ResumoRebanhoDTO resumo = useCase.executar();

        assertThat(resumo.total()).isEqualTo(4L);
        assertThat(resumo.porCategoria()).containsEntry(Categoria.BOI, 4L);
    }

    @Test
    @DisplayName("Contagem por categoria deve estar correta")
    void contagemPorCategoriaDeveEstarCorreta() {
        when(animalRepository.contarAtivosPorCategoria()).thenReturn(Map.of(
                Categoria.BEZERRO, 1L,
                Categoria.BEZERRA, 2L,
                Categoria.GARROTE, 3L,
                Categoria.NOVILHA, 4L,
                Categoria.VACA, 5L,
                Categoria.TOURO, 6L,
                Categoria.BOI, 7L
        ));

        ResumoRebanhoDTO resumo = useCase.executar();

        assertThat(resumo.porCategoria())
                .containsEntry(Categoria.BEZERRO, 1L)
                .containsEntry(Categoria.BEZERRA, 2L)
                .containsEntry(Categoria.GARROTE, 3L)
                .containsEntry(Categoria.NOVILHA, 4L)
                .containsEntry(Categoria.VACA, 5L)
                .containsEntry(Categoria.TOURO, 6L)
                .containsEntry(Categoria.BOI, 7L);
    }

    @Test
    @DisplayName("Categorias sem animais devem retornar zero")
    void categoriasSemAnimaisDevemRetornarZero() {
        when(animalRepository.contarAtivosPorCategoria()).thenReturn(Map.of(
                Categoria.VACA, 5L
        ));

        ResumoRebanhoDTO resumo = useCase.executar();

        assertThat(resumo.porCategoria()).containsEntry(Categoria.VACA, 5L);
        assertThat(resumo.porCategoria()).containsEntry(Categoria.BEZERRO, 0L);
        assertThat(resumo.porCategoria()).containsEntry(Categoria.BEZERRA, 0L);
        assertThat(resumo.porCategoria()).containsEntry(Categoria.GARROTE, 0L);
        assertThat(resumo.porCategoria()).containsEntry(Categoria.NOVILHA, 0L);
        assertThat(resumo.porCategoria()).containsEntry(Categoria.TOURO, 0L);
        assertThat(resumo.porCategoria()).containsEntry(Categoria.BOI, 0L);
    }
}
