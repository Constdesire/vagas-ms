package com.fatec.vagas.repository;

import com.fatec.vagas.entity.Alocacao;
import com.fatec.vagas.repository.adapter.AlocacaoRepositoryAdapter;
import com.fatec.vagas.repository.jpa.AlocacaoRepositoryWithJpa;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class AlocacaoRepositoryImpl implements AlocacaoRepository {

    private final AlocacaoRepositoryWithJpa jpa;

    public AlocacaoRepositoryImpl(AlocacaoRepositoryWithJpa jpa) {
        this.jpa = jpa;
    }

    @Override
    public Alocacao save(Alocacao alocacao) {
        return AlocacaoRepositoryAdapter.castOrm(jpa.save(AlocacaoRepositoryAdapter.castEntity(alocacao)));
    }

    @Override
    public Optional<Alocacao> findByCandidaturaId(String candidaturaId) {
        return jpa.findByCandidaturaId(candidaturaId).map(AlocacaoRepositoryAdapter::castOrm);
    }

    @Override
    public long countByVagaId(Long vagaId) {
        return jpa.countByVagaId(vagaId);
    }
}
