package com.fatec.vagas;

import com.fatec.vagas.entity.ResultadoReducao;
import com.fatec.vagas.entity.Vaga;
import com.fatec.vagas.repository.AlocacaoRepository;
import com.fatec.vagas.repository.VagaRepository;
import com.fatec.vagas.service.VagaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
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
        "vagas.dados-iniciais=false"
})
class VagaConcorrenciaTest {

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
}
