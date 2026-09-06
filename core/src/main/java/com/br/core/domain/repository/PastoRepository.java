package com.br.core.domain.repository;

import com.br.core.domain.model.Animal;
import com.br.core.domain.model.Pagina;
import com.br.core.domain.model.Pasto;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PastoRepository {
    void salvar(Pasto pasto);
    Optional<Pasto> buscarPorId(UUID id);
    List<Pasto> buscarTodos();
    Double buscarAreaHectares(UUID pastoId);
    Double somarAreaTotal();
    Pagina<Pasto> buscarTodosPaginado(int pagina, int tamanho);
}
