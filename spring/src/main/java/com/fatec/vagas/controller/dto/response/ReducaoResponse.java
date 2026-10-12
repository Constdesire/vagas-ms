package com.fatec.vagas.controller.dto.response;

import java.math.BigDecimal;

/** aceita = true: a vaga foi alocada (e deixou de existir). aceita = false: não havia vaga. */
public record ReducaoResponse(
        boolean aceita,
        String motivo,
        Long vagaId,
        String titulo,
        BigDecimal valor,
        long inicioEm,
        int disponiveis) {
}
