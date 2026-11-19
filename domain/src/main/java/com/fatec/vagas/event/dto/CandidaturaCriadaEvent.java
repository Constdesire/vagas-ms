package com.fatec.vagas.event.dto;

/** Evento que o candidatura-ms publica quando alguém se candidata (consumido aqui, no vagas-ms). */
public record CandidaturaCriadaEvent(String candidaturaId, Long vagaId, String usuario) {
}
