package com.fiap.financecontrol.gateways;

import com.fiap.financecontrol.domains.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByEmail(String email);

    Optional<Usuario> findByCpfCnpj(String cpfCnpj);

    List<Usuario> findByAtivo(String ativo);

    Page<Usuario> findByAtivo(String ativo, Pageable pageable);

    @Query("SELECT c FROM Usuario c WHERE c.nome LIKE %:nome%")
    List<Usuario> Usuario(@Param("nome") String nome);

    @Query("SELECT c FROM Usuario c WHERE c.nome LIKE %:nome% AND c.ativo = :ativo")
    Page<Usuario> findByNomeContainingAndAtivo(@Param("nome") String nome, @Param("ativo") String ativo, Pageable pageable);

    boolean existsByEmail(String email);

    boolean existsByCpfCnpj(String cpfCnpj);
}
