package com.fatec.vagas;

import com.fatec.vagas.entity.ResultadoReducao;
import com.fatec.vagas.entity.Vaga;
import com.fatec.vagas.event.ResultadoPublicador;
import com.fatec.vagas.event.dto.CandidaturaCriadaEvent;
import com.fatec.vagas.event.dto.CandidaturaResultadoEvent;
import com.fatec.vagas.repository.AlocacaoRepository;
import com.fatec.vagas.repository.VagaRepository;
import com.fatec.vagas.service.VagaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * O cenário da tarefa: várias requisições AO MESMO TEMPO disputando as mesmas vagas.
 * Todas as threads são soltas juntas (CountDownLatch) e o lock pessimista garante o resultado exato.
 */
@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:vagastest;DB_CLOSE_DELAY=-1;LOCK_TIMEOUT=30000",
        "vagas.dados-iniciais=false",
        // o teste não deve ler nem escrever no RabbitMQ de verdade (CloudAMQP)
        "spring.rabbitmq.listener.simple.auto-startup=false"
})
class VagaConcorrenciaTest {

    /** Publicador de mentira: guarda os resultados em memória em vez de mandar para o RabbitMQ. */
    static class PublicadorEmMemoria implements ResultadoPublicador {
        final List<CandidaturaResultadoEvent> enviados = new CopyOnWriteArrayList<>();

        @Override
        public void enviar(CandidaturaResultadoEvent evento) {
            enviados.add(evento);
        }
    }

    @TestConfiguration
    static class PublicadorDeTeste {
        @Bean
        @Primary
        PublicadorEmMemoria publicadorEmMemoria() {
            return new PublicadorEmMemoria();
        }
    }

    @Autowired
    private PublicadorEmMemoria publicador;

    @Autowired
    private VagaService vagaService;
    @Autowired
    private VagaRepository vagaRepository;
    @Autowired
    private AlocacaoRepository alocacaoRepository;

    private Long criarVaga(int capacidade) {
        Vaga nova = new Vaga(null, "Aula de teste", "teste", null, "EDUCACAO", "Lab",
                Instant.now().plusSeconds(3600), capacidade, capacidade, new BigDecimal("10.00"));
        return vagaService.criar(nova).id();
    }

    private List<ResultadoReducao> disparar(Long vagaId, int pedidos) throws Exception {
        ExecutorService pool = Executors.newFixedThreadPool(pedidos);
        CountDownLatch largada = new CountDownLatch(1);
        List<Future<ResultadoReducao>> futuros = new ArrayList<>();
        for (int i = 0; i < pedidos; i++) {
            String candidatura = "cand-" + vagaId + "-" + i;
            String usuario = "aluno" + i;
            futuros.add(pool.submit(() -> {
                largada.await();
                return vagaService.reduzir(vagaId, candidatura, usuario);
            }));
        }
        largada.countDown();
        List<ResultadoReducao> respostas = new ArrayList<>();
        for (Future<ResultadoReducao> f : futuros) {
            respostas.add(f.get());
        }
        pool.shutdown();
        return respostas;
    }

    @Test
    void oitentaPedidosParaQuarentaVagasAlocaExatamenteQuarenta() throws Exception {
        Long vagaId = criarVaga(40);

        List<ResultadoReducao> respostas = disparar(vagaId, 80);

        long aceitas = respostas.stream().filter(ResultadoReducao::aceita).count();
        assertEquals(40, aceitas, "exatamente 40 candidaturas devem ganhar vaga");
        assertEquals(40, respostas.size() - aceitas, "as outras 40 ficam de fora");
        assertEquals(0, vagaRepository.findById(vagaId).orElseThrow().disponiveis());
        assertEquals(40, alocacaoRepository.countByVagaId(vagaId));
    }

    @Test
    void doisPedidosSimultaneosParaUmaVagaSoUmGanha() throws Exception {
        Long vagaId = criarVaga(1);

        List<ResultadoReducao> respostas = disparar(vagaId, 2);

        assertEquals(1, respostas.stream().filter(ResultadoReducao::aceita).count());
        assertEquals(0, vagaRepository.findById(vagaId).orElseThrow().disponiveis());
    }

    @Test
    void mesmaCandidaturaProcessadaDuasVezesNaoGastaDuasVagas() {
        Long vagaId = criarVaga(5);

        ResultadoReducao primeira = vagaService.reduzir(vagaId, "cand-repetida", "ana");
        ResultadoReducao repetida = vagaService.reduzir(vagaId, "cand-repetida", "ana");

        assertTrue(primeira.aceita());
        assertTrue(repetida.aceita());
        assertEquals(4, vagaRepository.findById(vagaId).orElseThrow().disponiveis());
        assertEquals(1, alocacaoRepository.countByVagaId(vagaId));
    }

    @Test
    void candidaturaRecebidaDaFilaGeraConfirmadaERecusadaSemGastarVagaExtra() {
        Long vagaId = criarVaga(1);
        publicador.enviados.clear();

        vagaService.processarCandidatura(new CandidaturaCriadaEvent("evt-1", vagaId, "ana"));
        vagaService.processarCandidatura(new CandidaturaCriadaEvent("evt-2", vagaId, "bia"));
        vagaService.processarCandidatura(new CandidaturaCriadaEvent("evt-1", vagaId, "ana")); // mensagem repetida

        assertEquals(3, publicador.enviados.size());
        assertEquals("CONFIRMADA", publicador.enviados.get(0).status());
        assertEquals("RECUSADA", publicador.enviados.get(1).status());
        assertEquals("CONFIRMADA", publicador.enviados.get(2).status(), "a repetição da ana continua confirmada");
        assertEquals(0, vagaRepository.findById(vagaId).orElseThrow().disponiveis());
        assertEquals(1, alocacaoRepository.countByVagaId(vagaId), "só uma alocação, mesmo com mensagem repetida");
    }

    @Test
    void candidaturaParaVagaInexistenteViraRecusada() {
        publicador.enviados.clear();

        vagaService.processarCandidatura(new CandidaturaCriadaEvent("evt-x", 999999L, "ana"));

        assertEquals(1, publicador.enviados.size());
        assertEquals("RECUSADA", publicador.enviados.get(0).status());
    }
}
