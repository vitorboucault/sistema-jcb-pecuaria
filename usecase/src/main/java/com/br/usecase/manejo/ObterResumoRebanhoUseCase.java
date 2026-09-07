package com.br.usecase.manejo;

import com.br.core.domain.enums.Categoria;
import com.br.core.domain.repository.AnimalRepository;
import com.br.usecase.dto.ResumoRebanhoDTO;
import jakarta.inject.Named;

import java.util.EnumMap;
import java.util.Map;

@Named
public class ObterResumoRebanhoUseCase {

    private final AnimalRepository animalRepository;

    public ObterResumoRebanhoUseCase(AnimalRepository animalRepository) {
        this.animalRepository = animalRepository;
    }

    public ResumoRebanhoDTO executar() {
        Map<Categoria, Long> contagemAgregada = animalRepository.contarAtivosPorCategoria();
        Map<Categoria, Long> porCategoria = new EnumMap<>(Categoria.class);

        for (Categoria categoria : Categoria.values()) {
            porCategoria.put(categoria, contagemAgregada.getOrDefault(categoria, 0L));
        }

        long total = porCategoria.values().stream()
                .mapToLong(Long::longValue)
                .sum();

        return new ResumoRebanhoDTO(total, porCategoria);
    }
}
