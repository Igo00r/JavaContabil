package com.fiap.financecontrol.repositories;

import com.fiap.financecontrol.domains.RegistroContabil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RegistroContabilRepository extends JpaRepository<RegistroContabil, Long> {

    List<RegistroContabil> findByContaId(Long contaId);

    List<RegistroContabil> findByCentroCustoId(Long centroCustoId);

    Page<RegistroContabil> findByContaId(Long contaId, Pageable pageable);

    Page<RegistroContabil> findByCentroCustoId(Long centroCustoId, Pageable pageable);

    @Query("SELECT rc FROM RegistroContabil rc WHERE rc.valor >= :valorMinimo")
    List<RegistroContabil> findByValorMaiorOuIgual(@Param("valorMinimo") BigDecimal valorMinimo);

    @Query("SELECT rc FROM RegistroContabil rc WHERE rc.valor BETWEEN :valorMinimo AND :valorMaximo")
    Page<RegistroContabil> findByValorBetween(@Param("valorMinimo") BigDecimal valorMinimo, 
                                            @Param("valorMaximo") BigDecimal valorMaximo, 
                                            Pageable pageable);

    @Query("SELECT rc FROM RegistroContabil rc WHERE rc.dataCriacao BETWEEN :dataInicio AND :dataFim")
    List<RegistroContabil> findByDataCriacaoBetween(@Param("dataInicio") LocalDateTime dataInicio, 
                                                   @Param("dataFim") LocalDateTime dataFim);

    @Query("SELECT SUM(rc.valor) FROM RegistroContabil rc WHERE rc.conta.id = :contaId")
    BigDecimal sumValorByContaId(@Param("contaId") Long contaId);

    @Query("SELECT SUM(rc.valor) FROM RegistroContabil rc WHERE rc.centroCusto.id = :centroCustoId")
    BigDecimal sumValorByCentroCustoId(@Param("centroCustoId") Long centroCustoId);


    @Query("SELECT r FROM RegistroContabil r WHERE r.centroCusto.id = :id " +
            "AND MONTH(r.dataCriacao) = :mes AND YEAR(r.dataCriacao) = :ano")
    List<RegistroContabil> findByCentroCustoAndPeriodo(Long id, int mes, int ano);
}
