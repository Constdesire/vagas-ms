package com.fatec.vagas.entity;

/** Resposta do pedido "retire UMA vaga" aceita = true (vaga alocada) ou false (não havia vaga). */
public record ResultadoReducao(boolean aceita, String motivo, Vaga vaga) {
}
