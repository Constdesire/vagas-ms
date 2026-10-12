package com.fatec.vagas.repository.adapter;

import com.fatec.vagas.entity.Alocacao;
import com.fatec.vagas.repository.orm.AlocacaoOrmJpa;

public class AlocacaoRepositoryAdapter {

    private AlocacaoRepositoryAdapter() {
    }

    public static Alocacao castOrm(AlocacaoOrmJpa orm) {
        return new Alocacao(
                orm.getId(),
                orm.getVagaId(),
                orm.getCandidaturaId(),
                orm.getUsuario(),
                orm.getValor(),
                orm.getAlocadaEm()
        );
    }

    public static AlocacaoOrmJpa castEntity(Alocacao entity) {
        AlocacaoOrmJpa orm = new AlocacaoOrmJpa();
        orm.setId(entity.id());
        orm.setVagaId(entity.vagaId());
        orm.setCandidaturaId(entity.candidaturaId());
        orm.setUsuario(entity.usuario());
        orm.setValor(entity.valor());
        orm.setAlocadaEm(entity.alocadaEm());
        return orm;
    }
}
