package com.fatec.vagas.repository.jpa;

import com.fatec.vagas.repository.orm.AlocacaoOrmJpa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AlocacaoRepositoryWithJpa extends JpaRepository<AlocacaoOrmJpa, Long> {
    Optional<AlocacaoOrmJpa> findByCandidaturaId(String candidaturaId);

    long countByVagaId(Long vagaId);
}
