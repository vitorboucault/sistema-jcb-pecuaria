package com.br.core.domain.repository;

import com.br.core.domain.model.Animal;
import com.br.core.domain.model.Lote;
import com.br.core.domain.model.Pagina;
import jakarta.inject.Named;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LoteRepository {
    void salvar(Lote lote);
    Optional<Lote> buscarPorId(UUID id);
    List<Lote> buscarAtivos();
    Pagina<Lote> buscarTodosPaginado(int pagina, int tamanho);
}
