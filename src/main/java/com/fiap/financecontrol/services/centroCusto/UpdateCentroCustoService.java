package com.fiap.financecontrol.services.centroCusto;

import com.fiap.financecontrol.domains.CentroCusto;
import com.fiap.financecontrol.exceptions.CentroCustoNaoEncontradoException;
import com.fiap.financecontrol.repositories.CentroCustoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdateCentroCustoService implements CentroCustoDataServiceInterface {

    private final CentroCustoRepository centroCustoRepository;

    @Override
    @Transactional
    public CentroCusto execute(CentroCusto centroCusto) {
        if (!centroCustoRepository.existsById(centroCusto.getId())) {
            throw new CentroCustoNaoEncontradoException("Centro de Custo não encontrado com ID: " + centroCusto.getId());
        }
        return centroCustoRepository.save(centroCusto);
    }
}