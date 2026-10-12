package com.fatec.vagas.repository.adapter;

import com.fatec.vagas.entity.Vaga;
import com.fatec.vagas.repository.orm.VagaOrmJpa;

public class VagaRepositoryAdapter {

    private VagaRepositoryAdapter() {
    }

    public static Vaga castOrm(VagaOrmJpa orm) {
        return new Vaga(
                orm.getId(),
                orm.getTitulo(),
                orm.getDescricao(),
                orm.getImagemUrl(),
                orm.getCategoria(),
                orm.getLugar(),
                orm.getInicioEm(),
                orm.getCapacidade(),
                orm.getDisponiveis(),
                orm.getValor()
        );
    }

    public static VagaOrmJpa castEntity(Vaga entity) {
        VagaOrmJpa orm = new VagaOrmJpa();
        orm.setId(entity.id());
        orm.setTitulo(entity.titulo());
        orm.setDescricao(entity.descricao());
        orm.setImagemUrl(entity.imagemUrl());
        orm.setCategoria(entity.categoria());
        orm.setLugar(entity.lugar());
        orm.setInicioEm(entity.inicioEm());
        orm.setCapacidade(entity.capacidade());
        orm.setDisponiveis(entity.disponiveis());
        orm.setValor(entity.valor());
        return orm;
    }
}
