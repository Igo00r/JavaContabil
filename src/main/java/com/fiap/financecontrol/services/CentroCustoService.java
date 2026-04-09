package com.fiap.financecontrol.services;

import com.fiap.financecontrol.domains.CentroCusto;
import com.fiap.financecontrol.gateways.CentroCustoRepository;
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
public class CentroCustoService {

    private final CentroCustoRepository centroCustoRepository;


    @Transactional
    public CentroCusto createCentroCusto(CentroCusto centroCusto) {
        return centroCustoRepository.save(centroCusto);
    }

    @Transactional
    public void delete(Long id) {
        if (!centroCustoRepository.existsById(id)) {
            throw new RuntimeException("Centro de Custo não encontrado com ID: " + id);
        }
        centroCustoRepository.deleteById(id);
    }

    public Optional<CentroCusto> findById(Long id) {
        return centroCustoRepository.findById(id);
    }

    public CentroCusto executeOrThrow(Long id) {
        return centroCustoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Centro de Custo não encontrado com ID: " + id));
    }

    public Page<CentroCusto> listarCentrosCusto(int page, int size, Sort.Direction direction) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, "nomeCentroCusto"));
        return centroCustoRepository.findAll(pageable);
    }

    public Page<CentroCusto> buscarCentrosCustoPorNome(String nome, int page, int size, Sort.Direction direction) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, "nomeCentroCusto"));
        return centroCustoRepository.findByNomeContaining(nome, pageable);
    }

    @Transactional
    public CentroCusto execute(CentroCusto centroCusto) {
        if (!centroCustoRepository.existsById(centroCusto.getId())) {
            throw new RuntimeException("Centro de Custo não encontrado com ID: " + centroCusto.getId());
        }
        return centroCustoRepository.save(centroCusto);
    }


}
