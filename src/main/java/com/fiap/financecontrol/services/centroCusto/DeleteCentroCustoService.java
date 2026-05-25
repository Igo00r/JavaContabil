package com.fiap.financecontrol.services.centroCusto;

import com.fiap.financecontrol.exceptions.CentroCustoNaoEncontradoException;
import com.fiap.financecontrol.repositories.CentroCustoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DeleteCentroCustoService {

    private final CentroCustoRepository centroCustoRepository;

    @Transactional
    public void execute(Long id) {
        if (!centroCustoRepository.existsById(id)) {
            throw new CentroCustoNaoEncontradoException("Centro de Custo não encontrado com ID: " + id);
        }
        centroCustoRepository.deleteById(id);
    }
}