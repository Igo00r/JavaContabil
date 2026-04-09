package com.fiap.financecontrol.gateways;

import com.fiap.financecontrol.domains.Usuario;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class UsuarioRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Test
    void shouldFindClienteByEmail() {
        // Given
        Usuario usuario = Usuario.builder()
                .nomeCliente("João Silva")
                .cpfCnpj("12345678901")
                .email("joao@email.com")
                .senha("senha123")
                .ativo("S")
                .dataCadastro(LocalDateTime.now())
                .build();
        
        entityManager.persistAndFlush(usuario);

        // When
        Optional<Usuario> found = usuarioRepository.findByEmail("joao@email.com");

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getNomeCliente()).isEqualTo("João Silva");
    }

    @Test
    void shouldFindClienteByCpfCnpj() {
        // Given
        Usuario usuario = Usuario.builder()
                .nomeCliente("Maria Santos")
                .cpfCnpj("98765432100")
                .email("maria@email.com")
                .senha("senha456")
                .ativo("S")
                .dataCadastro(LocalDateTime.now())
                .build();
        
        entityManager.persistAndFlush(usuario);

        // When
        Optional<Usuario> found = usuarioRepository.findByCpfCnpj("98765432100");

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getNomeCliente()).isEqualTo("Maria Santos");
    }

    @Test
    void shouldReturnEmptyWhenClienteNotFound() {
        // When
        Optional<Usuario> found = usuarioRepository.findByEmail("inexistente@email.com");

        // Then
        assertThat(found).isEmpty();
    }

    @Test
    void shouldCheckIfEmailExists() {
        // Given
        Usuario usuario = Usuario.builder()
                .nomeCliente("Pedro Costa")
                .cpfCnpj("11122233344")
                .email("pedro@email.com")
                .senha("senha789")
                .ativo("S")
                .dataCadastro(LocalDateTime.now())
                .build();
        
        entityManager.persistAndFlush(usuario);

        // When & Then
        assertThat(usuarioRepository.existsByEmail("pedro@email.com")).isTrue();
        assertThat(usuarioRepository.existsByEmail("inexistente@email.com")).isFalse();
    }
}
