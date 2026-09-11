package com.br.infra.persistence.adapter;

import com.br.core.domain.enums.Status;
import com.br.core.domain.model.Animal;
import com.br.core.domain.model.Pagina;
import com.br.infra.persistence.entity.AnimalEntity;
import com.br.infra.persistence.mapper.AnimalMapper;
import com.br.infra.persistence.repository.SpringDataAnimalRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AnimalRepositoryImplTest {

    @Test
    @DisplayName("@spec:AC-106 Adapter converte status para name e preserva metadados da página")
    void buscarPorStatusPaginadoConverteStatusEPreservaMetadados() {
        SpringDataAnimalRepository springDataRepository = mock(SpringDataAnimalRepository.class);
        AnimalMapper mapper = mock(AnimalMapper.class);
        AnimalEntity entity = mock(AnimalEntity.class);
        Animal animal = mock(Animal.class);
        PageRequest pageRequest = PageRequest.of(2, 5);
        when(mapper.toDomain(entity)).thenReturn(animal);
        when(springDataRepository.findByStatus("MORTO", pageRequest))
                .thenReturn(new PageImpl<>(List.of(entity), pageRequest, 11));

        AnimalRepositoryImpl repository = new AnimalRepositoryImpl(springDataRepository, mapper);

        Pagina<Animal> resultado = repository.buscarPorStatusPaginado(Status.MORTO, 2, 5);

        assertThat(resultado.conteudo()).containsExactly(animal);
        assertThat(resultado.numeroPagina()).isEqualTo(2);
        assertThat(resultado.tamanhoPagina()).isEqualTo(5);
        assertThat(resultado.totalElementos()).isEqualTo(11);
        assertThat(resultado.totalPaginas()).isEqualTo(3);
        verify(springDataRepository).findByStatus(eq("MORTO"), eq(pageRequest));
    }

    @Test
    @DisplayName("@spec:AC-203 Adapter busca animais do lote somente com status ATIVO")
    void buscarPorLoteFiltraAnimaisAtivos() {
        SpringDataAnimalRepository springDataRepository = mock(SpringDataAnimalRepository.class);
        AnimalMapper mapper = mock(AnimalMapper.class);
        AnimalEntity entity = mock(AnimalEntity.class);
        Animal animal = mock(Animal.class);
        UUID loteId = UUID.randomUUID();
        when(springDataRepository.findByLoteAtualAndStatus(loteId, "ATIVO"))
                .thenReturn(List.of(entity));
        when(mapper.toDomain(entity)).thenReturn(animal);

        AnimalRepositoryImpl repository = new AnimalRepositoryImpl(springDataRepository, mapper);

        assertThat(repository.buscarPorLote(loteId)).containsExactly(animal);
        verify(springDataRepository).findByLoteAtualAndStatus(loteId, "ATIVO");
    }
}
