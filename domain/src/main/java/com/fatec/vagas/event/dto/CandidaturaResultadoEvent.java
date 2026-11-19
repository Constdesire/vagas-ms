package com.fatec.vagas.event.dto;

import java.math.BigDecimal;

/** Evento que o vagas-ms publica com a resposta: CONFIRMADA (vaga alocada) ou RECUSADA. */
public record CandidaturaResultadoEvent(
        String candidaturaId,
        Long vagaId,
        String usuario,
        String status,
        String motivo,
        String vagaTitulo,
        BigDecimal valor,
        Long vagaInicioEm) {
}
