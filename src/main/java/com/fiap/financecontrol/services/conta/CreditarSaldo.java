package com.fiap.financecontrol.services.conta;

import com.fiap.financecontrol.domains.Conta;
import com.fiap.financecontrol.exceptions.ContaNaoEncontradaException;
import com.fiap.financecontrol.repositories.ContaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class CreditarSaldo {
    private final ContaRepository contaRepository;

    @Transactional
    public void creditar(Long idConta, BigDecimal valor) {
        Conta conta = contaRepository.findById(idConta)
                .orElseThrow(() -> new ContaNaoEncontradaException("Conta nao encontrada:" + idConta));

        conta.setSaldo(conta.getSaldo().add(valor));
        contaRepository.save(conta);
    }
}
