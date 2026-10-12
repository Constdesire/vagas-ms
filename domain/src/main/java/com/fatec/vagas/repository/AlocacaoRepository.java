package com.fatec.vagas.repository;

import com.fatec.vagas.entity.Alocacao;

import java.util.Optional;

public interface AlocacaoRepository {
    Alocacao save(Alocacao alocacao);

    Optional<Alocacao> findByCandidaturaId(String candidaturaId);

    long countByVagaId(Long vagaId);
}
