package com.fiap.financecontrol.services.centroCusto;


import com.fiap.financecontrol.domains.CentroCusto;
import com.fiap.financecontrol.exceptions.CentroCustoNaoEncontradoException;
import com.fiap.financecontrol.repositories.CentroCustoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FindByIdCentroCustoService {

    private final CentroCustoRepository centroCustoRepository;

    public Optional<CentroCusto> execute(Long id) {
        return centroCustoRepository.findById(id);
    }

    public CentroCusto executeOrThrow(Long id) {
        return centroCustoRepository.findById(id)
                .orElseThrow(() -> new CentroCustoNaoEncontradoException("Centro de Custo não encontrado com ID: " + id));
    }
}
