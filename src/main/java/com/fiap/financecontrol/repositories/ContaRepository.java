package com.fiap.financecontrol.repositories;

import com.fiap.financecontrol.domains.Conta;
import com.fiap.financecontrol.domains.TipoConta;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContaRepository extends JpaRepository<Conta, Long> {

    List<Conta> findByTipo(TipoConta tipo);

    Page<Conta> findByTipo(TipoConta tipo, Pageable pageable);

    List<Conta> findByUsuarioId(Long clienteId);

    List<Conta> findByNomeContaContaining(String nome);

    @Query("SELECT c FROM Conta c WHERE c.nomeConta LIKE %:nome% AND c.tipo = :tipo")
    Page<Conta> findByNomeContainingAndTipo(@Param("nome") String nome, @Param("tipo") TipoConta tipo, Pageable pageable);

    @Query("SELECT c FROM Conta c WHERE c.usuario IS NULL")
    List<Conta> findContasGenericas();

    @Query("SELECT c FROM Conta c WHERE c.usuario IS NOT NULL")
    List<Conta> findContasEspecificas();
}
