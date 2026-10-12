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

/** Versão "do banco" da Alocacao (tabela ALOCACAO no H2). */
@Getter
@Setter
@Entity
@Table(name = "alocacao")
public class AlocacaoOrmJpa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long vagaId;

    // unique: se a mesma mensagem chegar duas vezes, a vaga nunca é retirada duas vezes
    @Column(unique = true)
    private String candidaturaId;

    private String usuario;

    @Column(precision = 10, scale = 2)
    private BigDecimal valor;

    private Instant alocadaEm;
}
