package com.fiap.financecontrol.gateways;

import com.fiap.financecontrol.domains.Vendas;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VendasRepository extends JpaRepository<Vendas, Long> {

    List<Vendas> findByUsuarioId(Long usuarioId);

    List<Vendas> findByRegistroContabilId(Long registroContabilId);

    Page<Vendas> findByUsuarioId(Long usuarioId, Pageable pageable);

    Page<Vendas> findByRegistroContabilId(Long registroContabilId, Pageable pageable);

    @Query("SELECT v FROM Vendas v WHERE v.usuario.nome LIKE %:nome%")
    List<Vendas> findByUsuarioNomeContaining(@Param("nome") String nome);

    @Query("SELECT v FROM Vendas v WHERE v.usuario.email = :email")
    List<Vendas> findByUsuarioEmail(@Param("email") String email);

    @Query("SELECT COUNT(v) FROM Vendas v WHERE v.usuario.id = :usuarioId")
    Long countByUsuarioId(@Param("usuarioId") Long usuarioId);

    @Query("SELECT v FROM Vendas v JOIN v.registroContabil rc WHERE rc.valor >= :valorMinimo")
    List<Vendas> findByValorRegistroMaiorOuIgual(@Param("valorMinimo") java.math.BigDecimal valorMinimo);
}
