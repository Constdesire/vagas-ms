package com.fatec.vagas.controller.adapter;

import com.fatec.vagas.controller.dto.request.VagaRequest;
import com.fatec.vagas.controller.dto.response.VagaResponse;
import com.fatec.vagas.entity.Vaga;

import java.time.Instant;

/** Converte entre o JSON da API (dto) e as classes do domain, como o LoginControllerAdapter. */
public class VagaControllerAdapter {
    private VagaControllerAdapter() {
    }

    public static Vaga castRequest(VagaRequest request) {
        return new Vaga(
                null,
                request.titulo(),
                request.descricao(),
                request.imagemUrl(),
                request.categoria(),
                request.lugar(),
                Instant.now().plusSeconds(request.minutosAteInicio() * 60L),
                request.capacidade(),
                request.capacidade(),
                request.valor()
        );
    }

    public static VagaResponse castResponse(Vaga v) {
        return new VagaResponse(
                v.id(),
                v.titulo(),
                v.descricao(),
                v.imagemUrl(),
                v.categoria(),
                v.lugar(),
                v.inicioEm().toEpochMilli(),
                v.capacidade(),
                v.disponiveis(),
                v.valor()
        );
    }
}
