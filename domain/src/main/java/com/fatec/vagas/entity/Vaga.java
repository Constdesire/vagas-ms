package com.fatec.vagas.entity;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Uma vaga do catálogo. "disponiveis" só muda dentro da transação com lock (VagaService.reduzir).
 */
public record Vaga(
        Long id,
        String titulo,
        String descricao,
        String imagemUrl,
        String categoria,
        String lugar,
        Instant inicioEm,
        int capacidade,
        int disponiveis,
        BigDecimal valor
) {
    public boolean temVaga() {
        return disponiveis > 0;
    }

    /** Devolve a vaga com UMA unidade a menos . */
    public Vaga retirarUma() {
        return new Vaga(id, titulo, descricao, imagemUrl, categoria, lugar, inicioEm, capacidade,
                disponiveis - 1, valor);
    }
}
