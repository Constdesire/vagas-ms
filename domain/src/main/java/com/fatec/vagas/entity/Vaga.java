package com.fatec.vagas.entity;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Uma vaga do catálogo. O domain é Java puro: nada de JPA aqui (a versão com @Entity fica no módulo spring).
 * "disponiveis" só muda dentro da transação com lock (VagaService.reduzir).
 */
public record Vaga(
        Long id,
        String titulo,
        String descricao,
        String imagemUrl,
        String categoria,   // EDUCACAO | SAUDE | ESPORTE | EVENTO | SERVICO | OUTROS
        String lugar,
        Instant inicioEm,   // data da aula/atividade (usada no relatório por mês)
        int capacidade,
        int disponiveis,    // vagas que ainda existem
        BigDecimal valor    // quanto custa para entrar
) {
    public boolean temVaga() {
        return disponiveis > 0;
    }

    /** Devolve a vaga com UMA unidade a menos (o record é imutável). */
    public Vaga retirarUma() {
        return new Vaga(id, titulo, descricao, imagemUrl, categoria, lugar, inicioEm, capacidade,
                disponiveis - 1, valor);
    }
}
