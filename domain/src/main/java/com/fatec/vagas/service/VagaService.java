package com.fatec.vagas.service;

import com.fatec.vagas.entity.Alocacao;
import com.fatec.vagas.entity.ResultadoReducao;
import com.fatec.vagas.entity.Vaga;
import com.fatec.vagas.repository.AlocacaoRepository;
import com.fatec.vagas.repository.VagaRepository;
import com.fatec.vagas.service.exception.NaoEncontradoException;
import com.fatec.vagas.service.exception.RegraNegocioException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.List;
import java.util.Set;

public class VagaService {

    private static final Logger log = LoggerFactory.getLogger(VagaService.class);
    private static final Set<String> CATEGORIAS = Set.of("EDUCACAO", "SAUDE", "ESPORTE", "EVENTO", "SERVICO", "OUTROS");
    private static final long UM_ANO_EM_SEGUNDOS = 525600L * 60L;

    private final VagaRepository vagaRepository;
    private final AlocacaoRepository alocacaoRepository;

    public VagaService(VagaRepository vagaRepository, AlocacaoRepository alocacaoRepository) {
        this.vagaRepository = vagaRepository;
        this.alocacaoRepository = alocacaoRepository;
    }

    @Transactional(readOnly = true)
    public List<Vaga> listar() {
        return vagaRepository.findAllByOrderByInicioEmAsc();
    }

    @Transactional(readOnly = true)
    public Vaga buscar(Long id) {
        return vagaRepository.findById(id).orElseThrow(() -> new NaoEncontradoException("Vaga não encontrada"));
    }

    /** Só o ADMIN chega aqui (/fatec/admin/vagas no gateway). */
    @Transactional
    public Vaga criar(Vaga nova) {
        if (nova.titulo() == null || nova.titulo().isBlank()) {
            throw new RegraNegocioException("Informe o título da vaga");
        }
        if (nova.capacidade() < 1 || nova.capacidade() > 1000) {
            throw new RegraNegocioException("A quantidade de vagas deve ficar entre 1 e 1000");
        }
        if (nova.valor() == null || nova.valor().signum() < 0 || nova.valor().compareTo(new BigDecimal("10000")) > 0) {
            throw new RegraNegocioException("O valor deve ficar entre R$ 0,00 e R$ 10.000,00");
        }
        String categoria = nova.categoria() == null ? "OUTROS" : nova.categoria();
        if (!CATEGORIAS.contains(categoria)) {
            throw new RegraNegocioException("Categoria inválida");
        }
        Instant agora = Instant.now();
        if (nova.inicioEm() == null || !nova.inicioEm().isAfter(agora)
                || nova.inicioEm().isAfter(agora.plusSeconds(UM_ANO_EM_SEGUNDOS))) {
            throw new RegraNegocioException("A data da vaga deve ser dentro de 1 ano");
        }
        Vaga vaga = new Vaga(null, nova.titulo().trim(), nova.descricao(), nova.imagemUrl(), categoria, nova.lugar(),
                nova.inicioEm(), nova.capacidade(), nova.capacidade(),   // começa com TODAS as vagas livres
                nova.valor().setScale(2, RoundingMode.HALF_UP));
        Vaga salva = vagaRepository.save(vaga);
        log.info("[VAGAS  ] vaga #{} criada: \"{}\" - {} vaga(s) a R$ {}", salva.id(), salva.titulo(),
                salva.capacidade(), salva.valor());
        return salva;
    }

    /**
     * REDUÇÃO DE VAGA (a parte que exige concorrência).
     * Várias candidaturas chegam ao mesmo tempo (threads diferentes). O SELECT ... FOR UPDATE
     * (findByIdWithLock) faz uma esperar a outra: dentro do lock lemos "disponiveis", retiramos UMA vaga e
     * gravamos. O lock só é solto no commit. Por isso nunca se aloca mais vagas do que existem.
     */
    @Transactional
    public ResultadoReducao reduzir(Long vagaId, String candidaturaId, String usuario) {
        String thread = Thread.currentThread().getName();
        log.info("[REDUCAO] thread={} pedindo o LOCK da vaga #{} (candidatura {})", thread, vagaId, candidaturaId);
        Vaga vaga = vagaRepository.findByIdWithLock(vagaId)
                .orElseThrow(() -> new NaoEncontradoException("Vaga não encontrada"));
        log.info("[LOCK   ] thread={} OBTEVE o lock da vaga #{}: {} de {} disponíveis", thread, vagaId,
                vaga.disponiveis(), vaga.capacidade());

        // mensagem repetida (o RabbitMQ pode entregar de novo): a mesma candidatura nunca gasta duas vagas
        if (alocacaoRepository.findByCandidaturaId(candidaturaId).isPresent()) {
            log.info("[REDUCAO] thread={} candidatura {} já tinha vaga alocada; nada a fazer", thread, candidaturaId);
            return new ResultadoReducao(true, null, vaga);
        }

        if (!vaga.temVaga()) {
            log.info("[REDUCAO] thread={} RECUSADO {}: vagas esgotadas na vaga #{}", thread, usuario, vagaId);
            AposCommit.avisar("thread=" + thread + " recusou " + usuario + " (sem vagas); lock liberado");
            return new ResultadoReducao(false, "As vagas acabaram", vaga);
        }

        Vaga atualizada = vagaRepository.save(vaga.retirarUma());
        alocacaoRepository.save(new Alocacao(null, vagaId, candidaturaId, usuario, vaga.valor(), Instant.now()));

        log.info("[REDUCAO] thread={} ALOCADA 1 vaga para {} (restam {})", thread, usuario, atualizada.disponiveis());
        AposCommit.avisar("thread=" + thread + " alocou vaga para " + usuario + " (restam "
                + atualizada.disponiveis() + "); lock liberado");
        return new ResultadoReducao(true, null, atualizada);
    }
}
