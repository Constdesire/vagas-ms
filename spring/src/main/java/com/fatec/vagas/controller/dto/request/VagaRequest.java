package com.fatec.vagas.controller.dto.request;

import java.math.BigDecimal;

public record VagaRequest(
        String titulo,
        String descricao,
        String imagemUrl,
        String categoria,
        String lugar,
        int capacidade,
        BigDecimal valor,
        int minutosAteInicio) {
}
