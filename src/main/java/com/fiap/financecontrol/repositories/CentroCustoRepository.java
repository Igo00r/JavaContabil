package com.fiap.financecontrol.repositories;

import com.fiap.financecontrol.domains.CentroCusto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CentroCustoRepository extends JpaRepository<CentroCusto, Long> {

    List<CentroCusto> findByNomeCentroCustoContaining(String nome);

    @Query("SELECT cc FROM CentroCusto cc WHERE cc.nomeCentroCusto LIKE %:nome%")
    Page<CentroCusto> findByNomeContaining(@Param("nome") String nome, Pageable pageable);

    @Query("SELECT cc FROM CentroCusto cc ORDER BY cc.nomeCentroCusto ASC")
    List<CentroCusto> findAllOrderByNome();
}
