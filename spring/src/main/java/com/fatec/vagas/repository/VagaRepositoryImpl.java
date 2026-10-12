package com.fatec.vagas.repository;

import com.fatec.vagas.entity.Vaga;
import com.fatec.vagas.repository.adapter.VagaRepositoryAdapter;
import com.fatec.vagas.repository.jpa.VagaRepositoryWithJpa;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/** Implementa a porta do domain usando JPA/H2 (o domain não sabe disso). */
@Repository
public class VagaRepositoryImpl implements VagaRepository {

    private final VagaRepositoryWithJpa jpa;

    public VagaRepositoryImpl(VagaRepositoryWithJpa jpa) {
        this.jpa = jpa;
    }

    @Override
    public Vaga save(Vaga vaga) {
        return VagaRepositoryAdapter.castOrm(jpa.save(VagaRepositoryAdapter.castEntity(vaga)));
    }

    @Override
    public Optional<Vaga> findById(Long id) {
        return jpa.findById(id).map(VagaRepositoryAdapter::castOrm);
    }

    @Override
    public Optional<Vaga> findByIdWithLock(Long id) {
        return jpa.findByIdWithLock(id).map(VagaRepositoryAdapter::castOrm);
    }

    @Override
    public List<Vaga> findAllByOrderByInicioEmAsc() {
        return jpa.findAllByOrderByInicioEmAsc().stream().map(VagaRepositoryAdapter::castOrm).toList();
    }

    @Override
    public long count() {
        return jpa.count();
    }
}
