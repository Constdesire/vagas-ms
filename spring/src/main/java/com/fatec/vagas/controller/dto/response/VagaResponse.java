package com.fatec.vagas.controller.dto.response;

import java.math.BigDecimal;

public record VagaResponse(
        Long id,
        String titulo,
        String descricao,
        String imagemUrl,
        String categoria,
        String lugar,
        long inicioEm,
        int capacidade,
        int disponiveis,
        BigDecimal valor) {
}
