package com.br.core.domain.repository;

import com.br.core.domain.model.Animal;
import com.br.core.domain.model.Pagina;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AnimalRepository {

    void salvar(Animal animal);
    Optional<Animal> buscarPorId(UUID id);
    Optional<Animal> buscarPorBrinco(String brinco);
    List<Animal> buscarAnimaisElegiveisParaEvolucao();
    Pagina<Animal> buscarTodosPaginado(int pagina, int tamanho);
    List<Animal> buscarPorLote(UUID loteId);
    long contarAnimaisAtivos();
}
