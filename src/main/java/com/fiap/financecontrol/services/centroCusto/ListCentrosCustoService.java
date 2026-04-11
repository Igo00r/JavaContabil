package com.fiap.financecontrol.services.centroCusto;

import com.fiap.financecontrol.domains.CentroCusto;
import com.fiap.financecontrol.repositories.CentroCustoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ListCentrosCustoService {

    private final CentroCustoRepository centroCustoRepository;

    public Page<CentroCusto> listarCentrosCusto(int page, int size, Sort.Direction direction) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, "nomeCentroCusto"));
        return centroCustoRepository.findAll(pageable);
    }

    public Page<CentroCusto> buscarCentrosCustoPorNome(String nome, int page, int size, Sort.Direction direction) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, "nomeCentroCusto"));
        return centroCustoRepository.findByNomeContaining(nome, pageable);
    }
}