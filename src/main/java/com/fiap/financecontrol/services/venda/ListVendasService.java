package com.fiap.financecontrol.services.venda;

import com.fiap.financecontrol.domains.Vendas;
import com.fiap.financecontrol.repositories.VendasRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ListVendasService {

    private final VendasRepository vendasRepository;

    public Page<Vendas> listarVendas(int page, int size, Sort.Direction direction) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, "id"));
        return vendasRepository.findAll(pageable);
    }

    public Page<Vendas> listarVendasPorCliente(Long clienteId, int page, int size, Sort.Direction direction) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, "id"));
        return vendasRepository.findByUsuarioId(clienteId, pageable);
    }

    public Page<Vendas> listarVendasPorRegistroContabil(Long registroContabilId, int page, int size, Sort.Direction direction) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, "id"));
        return vendasRepository.findByRegistroContabilId(registroContabilId, pageable);
    }
}