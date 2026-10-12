package com.fatec.vagas.repository.jpa;

import com.fatec.vagas.repository.orm.VagaOrmJpa;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VagaRepositoryWithJpa extends JpaRepository<VagaOrmJpa, Long> {

    // SELECT ... FOR UPDATE: só uma transação por vez mexe nas vagas desta linha
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT v FROM VagaOrmJpa v WHERE v.id = :id")
    Optional<VagaOrmJpa> findByIdWithLock(@Param("id") Long id);

    List<VagaOrmJpa> findAllByOrderByInicioEmAsc();
}
