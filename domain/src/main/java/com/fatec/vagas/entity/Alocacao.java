package com.fatec.vagas.entity;

import java.math.BigDecimal;
import java.time.Instant;

/** Registro de cada vaga que foi retirada "alocou essa vaga, ela não existe mais". */
public record Alocacao(
        Long id,
        Long vagaId,
        String candidaturaId,   // único: a mesma mensagem repetida nunca retira a vaga duas vezes
        String usuario,
        BigDecimal valor,
        Instant alocadaEm
) {
}
