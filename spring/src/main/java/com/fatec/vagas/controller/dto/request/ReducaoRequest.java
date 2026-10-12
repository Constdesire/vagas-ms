package com.fatec.vagas.controller.dto.request;

/** Pedido que a API de redução (reducao-ms) faz: "retire UMA vaga para esta candidatura". */
public record ReducaoRequest(String candidaturaId, String usuario) {
}
