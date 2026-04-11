package com.fiap.financecontrol.services.conta;

import com.fiap.financecontrol.repositories.ContaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Service
@RequiredArgsConstructor
public class DeleteContaService {

    private final ContaRepository contaRepository;

    @Transactional
    public void execute(Long id) {
        if (!contaRepository.existsById(id)) {
            throw new RuntimeException("Conta não encontrada com ID: " + id);
        }
        contaRepository.deleteById(id);
    }
}