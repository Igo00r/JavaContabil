package com.fiap.financecontrol.config;

import com.fiap.financecontrol.domains.*;
import com.fiap.financecontrol.repositories.CentroCustoRepository;
import com.fiap.financecontrol.repositories.ContaRepository;
import com.fiap.financecontrol.repositories.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;

@Configuration
public class DataInitializer {

    @Bean
    @Order(1)
    public CommandLineRunner criarAdmin(UsuarioRepository repository, PasswordEncoder encoder) {
        return args -> {

            if (repository.findByEmail("admin@email.com").isEmpty()) {
                System.out.println("Criando admin...");
                Usuario admin = new Usuario();
                admin.setNome("Admin");
                admin.setEmail("admin@email.com");
                admin.setSenha(encoder.encode("123"));
                admin.setCpfCnpj("22315678911");
                admin.setRole(Role.ADMIN);

                repository.save(admin);
            }
        };
    }

    @Bean
    @Order(2)
    public CommandLineRunner criadUsuarioNormal(UsuarioRepository repository, PasswordEncoder encoder) {
        return args -> {

            if (repository.findByEmail("usuarioNormal@email.com").isEmpty()) {
                System.out.println("Criando admin...");
                Usuario admin = new Usuario();
                admin.setNome("Admin");
                admin.setEmail("usuarioNormal@email.com");
                admin.setSenha(encoder.encode("123"));
                admin.setCpfCnpj("22315671911");
                admin.setRole(Role.USER);

                repository.save(admin);
            }
        };
    }

    @Bean
    @Order(3)
    public CommandLineRunner criarCentroCusto(CentroCustoRepository repository, PasswordEncoder encoder) {
        return args -> {

            CentroCusto centroCusto = new CentroCusto();
            centroCusto.setNomeCentroCusto("OPERACIONAL PADRAO");

            repository.save(centroCusto);
        };
    }

    @Bean
    @Order(4)
    public CommandLineRunner criarContaContabilReceita(
            ContaRepository contaRepository,
            UsuarioRepository usuarioRepository) {

        return args -> {

            Usuario usuario = usuarioRepository
                    .findByEmail("usuarioNormal@email.com")
                    .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

            Conta conta = new Conta();
            conta.setNomeConta("Conta Receita");
            conta.setTipo(TipoConta.R);
            conta.setSaldo(BigDecimal.ZERO);
            conta.setUsuario(usuario);

            contaRepository.save(conta);
        };
    }

    @Bean
    @Order(5)
    public CommandLineRunner criarContaContabilDespesa(
            ContaRepository contaRepository,
            UsuarioRepository usuarioRepository) {

        return args -> {

            Usuario usuario = usuarioRepository
                    .findByEmail("usuarioNormal@email.com")
                    .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

            Conta conta = new Conta();
            conta.setNomeConta("Conta Despesa");
            conta.setTipo(TipoConta.D);
            conta.setSaldo(BigDecimal.ZERO);
            conta.setUsuario(usuario);

            contaRepository.save(conta);
        };
    }
}
