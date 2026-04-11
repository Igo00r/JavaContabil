package com.fiap.financecontrol.services.venda;

import com.fiap.financecontrol.clients.ContaClient;
import com.fiap.financecontrol.clients.UsuarioClient;
import com.fiap.financecontrol.domains.*;
import com.fiap.financecontrol.repositories.*;
import com.fiap.financecontrol.presentation.dtos.response.UsuarioResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class CreateVendasService implements VendasDataServiceInterface {

    private final VendasRepository vendasRepository;
    private final UsuarioRepository usuarioRepository;
    private final RegistroContabilRepository registroContabilRepository;
    private final ContaRepository contaRepository;
    private final CentroCustoRepository centroCustoRepository;
    private final UsuarioClient usuarioClient;
    private final ContaClient contaClient;
    private final RabbitTemplate rabbitTemplate;

    @Transactional
    public Vendas execute(Vendas vendas) {

        validarUsuarioFeign(vendas);

        Usuario usuario = buscarUsuario(vendas);

        Long idConta = obterIdConta(vendas);
        Long idCentroCusto = obterIdCentroCusto(vendas);

        validarContaFeign(idConta);

        Conta conta = buscarConta(idConta);
        CentroCusto centroCusto = buscarCentroCusto(idCentroCusto);

        BigDecimal valorDaVenda = vendas.getValorTotal();

        atualizarSaldoConta(conta, valorDaVenda);

        RegistroContabil registro = criarRegistroContabil(conta, centroCusto, valorDaVenda);

        Vendas vendaSalva = salvarVenda(vendas, usuario, registro);

        // LOG: Enviando mensagem para a fila
        log.info("Enviando mensagem para fila: Venda criada ID {}", vendaSalva.getId());
        rabbitTemplate.convertAndSend("fila-vendas", "Venda criada: " + vendaSalva.getId());

        return vendaSalva;
    }

    private void validarUsuarioFeign(Vendas vendas) {
        Long usuarioId = vendas.getUsuario().getId();

        // LOG: Chamando o serviço
        log.info("Chamando serviço de usuário via Feign - ID: {}", usuarioId);

        UsuarioResponseDto usuarioResponse = usuarioClient.buscarClientePorId(usuarioId);

        if (usuarioResponse == null) {
            log.error("Falha: Usuário não encontrado via Feign - ID: {}", usuarioId);
            throw new RuntimeException("Usuário não encontrado via Feign");
        }

        // LOG: Validação com sucesso
        log.info("Usuário validado com sucesso via Feign");
    }

    private Usuario buscarUsuario(Vendas vendas) {
        return usuarioRepository.findById(vendas.getUsuario().getId())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado com ID: " + vendas.getUsuario().getId()));
    }

    private Long obterIdConta(Vendas vendas) {
        return vendas.getRegistroContabil().getConta().getId();
    }

    private Long obterIdCentroCusto(Vendas vendas) {
        return vendas.getRegistroContabil().getCentroCusto().getId();
    }

    private Conta buscarConta(Long idConta) {
        return contaRepository.findById(idConta)
                .orElseThrow(() -> new RuntimeException("Conta não encontrada com ID: " + idConta));
    }

    private CentroCusto buscarCentroCusto(Long idCentroCusto) {
        return centroCustoRepository.findById(idCentroCusto)
                .orElseThrow(() -> new RuntimeException("Centro de Custo não encontrado com ID: " + idCentroCusto));
    }

    private void atualizarSaldoConta(Conta conta, BigDecimal valor) {
        BigDecimal novoSaldo = conta.getSaldo().add(valor);
        conta.setSaldo(novoSaldo);
        contaRepository.save(conta);
    }

    private RegistroContabil criarRegistroContabil(Conta conta, CentroCusto centroCusto, BigDecimal valor) {
        RegistroContabil registro = new RegistroContabil();
        registro.setValor(valor);
        registro.setConta(conta);
        registro.setCentroCusto(centroCusto);
        registro.setDataCriacao(LocalDateTime.now());
        registro.setDataAtualizacao(LocalDateTime.now());

        return registroContabilRepository.save(registro);
    }

    private Vendas salvarVenda(Vendas vendas, Usuario usuario, RegistroContabil registro) {
        vendas.setUsuario(usuario);
        vendas.setRegistroContabil(registro);
        return vendasRepository.save(vendas);
    }

    private void validarContaFeign(Long idConta) {
        try {
            contaClient.buscarContaPorId(idConta);
        } catch (Exception e) {
            throw new RuntimeException("Conta não encontrada via Feign");
        }
    }
}