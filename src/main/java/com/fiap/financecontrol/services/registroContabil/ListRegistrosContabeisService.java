package com.fiap.financecontrol.services.registroContabil;

import com.fiap.financecontrol.domains.RegistroContabil;
import com.fiap.financecontrol.repositories.RegistroContabilRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class ListRegistrosContabeisService {

    private final RegistroContabilRepository registroContabilRepository;

    public Page<RegistroContabil> listarRegistrosContabeis(int page, int size, Sort.Direction direction) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, "dataCriacao"));
        return registroContabilRepository.findAll(pageable);
    }

    public Page<RegistroContabil> listarRegistrosPorConta(Long contaId, int page, int size, Sort.Direction direction) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, "dataCriacao"));
        return registroContabilRepository.findByContaId(contaId, pageable);
    }

    public Page<RegistroContabil> listarRegistrosPorCentroCusto(Long centroCustoId, int page, int size, Sort.Direction direction) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, "dataCriacao"));
        return registroContabilRepository.findByCentroCustoId(centroCustoId, pageable);
    }

    public Page<RegistroContabil> listarRegistrosPorValor(BigDecimal valorMinimo, BigDecimal valorMaximo, int page, int size, Sort.Direction direction) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, "dataCriacao"));
        return registroContabilRepository.findByValorBetween(valorMinimo, valorMaximo, pageable);
    }
}