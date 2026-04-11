package com.fiap.financecontrol.services.conta;

import com.fiap.financecontrol.domains.Conta;
import com.fiap.financecontrol.domains.TipoConta;
import com.fiap.financecontrol.repositories.ContaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ContaService {
    private final ContaRepository contaRepository;

    public Page<Conta> listarContas(int page, int size, Sort.Direction direction) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, "nomeConta"));
        return contaRepository.findAll(pageable);
    }

    public Page<Conta> listarContasPorTipo(TipoConta tipo, int page, int size, Sort.Direction direction) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, "nomeConta"));
        return contaRepository.findByTipo(tipo, pageable);
    }

    public Page<Conta> buscarContasPorNome(String nome, TipoConta tipo, int page, int size, Sort.Direction direction) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, "nomeConta"));
        return contaRepository.findByNomeContainingAndTipo(nome, tipo, pageable);
    }
    @Transactional
    public void deletarConta(Long id) {
        if (!contaRepository.existsById(id)) {
            throw new RuntimeException("Conta não encontrada com ID: " + id);
        }
        contaRepository.deleteById(id);
    }

    public Optional<Conta> execute(Long id) {
        return contaRepository.findById(id);
    }

    public Conta executeOrThrow(Long id) {
        return contaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Conta não encontrada com ID: " + id));
    }


}
