package com.br.core.domain.repository;

import com.br.core.domain.enums.Categoria;
import com.br.core.domain.model.Animal;
import com.br.core.domain.model.Pagina;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public interface AnimalRepository {

    Animal salvar(Animal animal);
    Optional<Animal> buscarPorId(UUID id);
    Optional<Animal> buscarPorBrinco(String brinco);
    List<Animal> buscarAnimaisElegiveisParaEvolucao();
    Pagina<Animal> buscarTodosPaginado(int pagina, int tamanho);
    List<Animal> buscarPorLote(UUID loteId);
    long contarAnimaisAtivos();
    Map<Categoria, Long> contarAtivosPorCategoria();
    void excluirPorId(UUID id);
    boolean existeFilhoComMaeId(UUID maeId);
}
