package com.fatec.vagas.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/** Só para o console mostra o momento exato em que o banco confirmou a transação e soltou o lock. */
final class AposCommit {

    private static final Logger log = LoggerFactory.getLogger(AposCommit.class);

    private AposCommit() {
    }

    static void avisar(String mensagem) {
        if (!TransactionSynchronizationManager.isSynchronizationActive()) {
            return;
        }
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                log.info("[COMMIT ] {}", mensagem);
            }
        });
    }
}
