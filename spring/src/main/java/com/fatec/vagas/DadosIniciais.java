package com.fatec.vagas;

import com.fatec.vagas.entity.Vaga;
import com.fatec.vagas.repository.VagaRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Instant;

/** Cria vagas de exemplo na primeira execução (banco vazio). */
@Component
@ConditionalOnProperty(name = "vagas.dados-iniciais", havingValue = "true")
public class DadosIniciais implements CommandLineRunner {

    private final VagaRepository repository;

    public DadosIniciais(VagaRepository repository) {
        this.repository = repository;
    }

    @Override
    public void run(String... args) {
        if (repository.count() > 0) {
            return;
        }
        criar("Aula no Laboratório 3", "40 vagas para a turma. Com 80 pessoas pedindo ao mesmo tempo, só 40 entram.",
                "EDUCACAO", "Laboratório 3", 40, "10.00", 2, "laboratorio");
        criar("Workshop de React Native", "Vaga única: ideal para ver dois pedidos simultâneos disputando a mesma vaga.",
                "EDUCACAO", "Sala 12", 1, "30.00", 8, "reactnative");
        criar("Monitoria de Banco de Dados", "Revisão de SQL, transações e locks.",
                "EDUCACAO", "Sala 5", 5, "15.00", 35, "bancodados");
        criar("Quadra de futsal", "Racha semanal. Traga a camisa de cor diferente.",
                "ESPORTE", "Quadra do bairro", 10, "8.00", 3, "futsal");
        criar("Palestra: IA na prática", "Como empresas usam IA no dia a dia.",
                "EVENTO", "Auditório", 20, "5.00", 40, "palestra");
    }

    private void criar(String titulo, String descricao, String categoria, String lugar, int capacidade,
                       String valor, int diasAteInicio, String semente) {
        repository.save(new Vaga(null, titulo, descricao,
                "https://picsum.photos/seed/" + semente + "/600/400", categoria, lugar,
                Instant.now().plusSeconds(diasAteInicio * 86400L), capacidade, capacidade, new BigDecimal(valor)));
    }
}
