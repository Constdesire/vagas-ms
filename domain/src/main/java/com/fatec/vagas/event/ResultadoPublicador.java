package com.fatec.vagas.event;

import com.fatec.vagas.event.dto.CandidaturaResultadoEvent;

/** Porta de saída: o domain pede "publique o resultado"; o RabbitMQ fica no módulo spring. */
public interface ResultadoPublicador {
    void enviar(CandidaturaResultadoEvent evento);
}
