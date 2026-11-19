package com.fatec.vagas.event;

import com.fatec.vagas.event.dto.CandidaturaCriadaEvent;
import com.fatec.vagas.service.VagaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/** Assinante do evento "candidatura-criada": cada mensagem vira UMA transação no VagaService. */
@Component
public class CandidaturaCriadaListener {

    private static final Logger log = LoggerFactory.getLogger(CandidaturaCriadaListener.class);

    private final VagaService service;
    private final long atrasoSimuladoMs;

    public CandidaturaCriadaListener(VagaService service,
                                     @Value("${vagas.atraso-simulado-ms:0}") long atrasoSimuladoMs) {
        this.service = service;
        this.atrasoSimuladoMs = atrasoSimuladoMs;
    }

    // concurrency: vários consumidores ao mesmo tempo -> várias candidaturas disputam a mesma vaga (o lock resolve)
    @RabbitListener(queues = "${event.candidatura-criada.queue}", concurrency = "${vagas.consumidores:4-8}")
    public void receber(CandidaturaCriadaEvent evento) {
        log.info("[RABBIT <<] RECEBIDO candidatura de {} para a vaga #{}", evento.usuario(), evento.vagaId());
        simularProcessamento();
        service.processarCandidatura(evento);
    }

    // só para a demonstração: deixa o status "em análise" visível por instantes no app (fora da transação)
    private void simularProcessamento() {
        if (atrasoSimuladoMs > 0) {
            try {
                Thread.sleep(atrasoSimuladoMs);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
