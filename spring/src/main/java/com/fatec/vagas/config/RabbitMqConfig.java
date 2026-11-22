package com.fatec.vagas.config;

import org.springframework.aot.hint.annotation.RegisterReflectionForBinding;
import com.fatec.vagas.event.dto.CandidaturaCriadaEvent;
import com.fatec.vagas.event.dto.CandidaturaResultadoEvent;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Mesmo padrão da aula (Queue + DirectExchange + Binding com a routing key = nome da fila).
 * Este serviço (vagas-ms) CONSOME "candidatura-criada" e PUBLICA em "candidatura-resultado".
 * As declarações são idempotentes: os outros serviços declaram as mesmas filas.
 */
// Native image: os eventos (records) sao convertidos para/de JSON pelo Jackson via reflexao,
// entao precisam ser registrados para o GraalVM. Sem isso o envio/leitura da fila falha no executavel nativo.
@RegisterReflectionForBinding({CandidaturaCriadaEvent.class, CandidaturaResultadoEvent.class})
@Configuration
public class RabbitMqConfig {

    // ---- fila de entrada: candidaturas para processar ----
    @Bean
    public Queue candidaturaCriadaQueue(@Value("${event.candidatura-criada.queue}") String queue) {
        return new Queue(queue, true);
    }

    @Bean
    public DirectExchange candidaturaCriadaExchange(@Value("${event.candidatura-criada.exchange}") String exchange) {
        return new DirectExchange(exchange, true, false);
    }

    @Bean
    public Binding candidaturaCriadaBinding(@Qualifier("candidaturaCriadaQueue") Queue queue,
                                            @Qualifier("candidaturaCriadaExchange") DirectExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(queue.getName());
    }

    // ---- fila de saída: resultado de cada candidatura ----
    @Bean
    public Queue candidaturaResultadoQueue(@Value("${event.candidatura-resultado.queue}") String queue) {
        return new Queue(queue, true);
    }

    @Bean
    public DirectExchange candidaturaResultadoExchange(@Value("${event.candidatura-resultado.exchange}") String exchange) {
        return new DirectExchange(exchange, true, false);
    }

    @Bean
    public Binding candidaturaResultadoBinding(@Qualifier("candidaturaResultadoQueue") Queue queue,
                                               @Qualifier("candidaturaResultadoExchange") DirectExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(queue.getName());
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new JacksonJsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, MessageConverter jsonMessageConverter) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter);
        return rabbitTemplate;
    }
}
