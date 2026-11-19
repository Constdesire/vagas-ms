package com.fatec.vagas.config;

import com.fatec.vagas.event.ResultadoPublicador;
import com.fatec.vagas.repository.AlocacaoRepository;
import com.fatec.vagas.repository.VagaRepository;
import com.fatec.vagas.service.VagaService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Cria o VagaService (classe do domain) como bean do Spring, igual ao LoginServiceConfig do login. */
@Configuration
public class VagaServiceConfig {

    @Bean
    public VagaService vagaService(VagaRepository vagaRepository, AlocacaoRepository alocacaoRepository,
                                   ResultadoPublicador resultadoPublicador) {
        return new VagaService(vagaRepository, alocacaoRepository, resultadoPublicador);
    }
}
