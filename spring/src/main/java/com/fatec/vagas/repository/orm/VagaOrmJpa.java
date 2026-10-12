package com.fatec.vagas.repository.orm;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

/** Versão "do banco" da Vaga (tabela VAGA no H2). A versão do domain é o record entity.Vaga. */
@Getter
@Setter
@Entity
@Table(name = "vaga")
public class VagaOrmJpa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titulo;

    private String descricao;

    private String imagemUrl;

    private String categoria;

    private String lugar;

    private Instant inicioEm;

    private int capacidade;

    private int disponiveis;

    @Column(precision = 10, scale = 2)
    private BigDecimal valor;
}
