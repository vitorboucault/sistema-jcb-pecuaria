package com.sistema.sistemajcb.infrastructure.persistence.repository;

import com.sistema.sistemajcb.infrastructure.persistence.entity.VendaAnimalEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface SpringDataVendaAnimalRepository extends JpaRepository<VendaAnimalEntity, UUID> {
    List<VendaAnimalEntity> findByDataVendaBetween(LocalDate inicio, LocalDate fim);
}
