package com.fatec.vagas.repository;

import com.fatec.vagas.entity.Vaga;

import java.util.List;
import java.util.Optional;

/** Porta de saída do domain quem implementa (JPA/H2) está no módulo spring. */
public interface VagaRepository {
    Vaga save(Vaga vaga);

    Optional<Vaga> findById(Long id);

    Optional<Vaga> findByIdWithLock(Long id);

    List<Vaga> findAllByOrderByInicioEmAsc();

    long count();
}
