package com.fiap.financecontrol.services.venda;

import com.fiap.financecontrol.domains.Vendas;
import com.fiap.financecontrol.repositories.VendasRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class VendaService {
    private final VendasRepository vendasRepository;

    @Transactional
    public void deleteVenda(Long id) {
        if (!vendasRepository.existsById(id)) {
            throw new RuntimeException("Venda não encontrada com ID: " + id);
        }
        vendasRepository.deleteById(id);
    }

    public Optional<Vendas> buscaPorIdVenda(Long id) {
        return vendasRepository.findById(id);
    }

    public Vendas executeOrThrow(Long id) {
        return vendasRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Venda não encontrada com ID: " + id));
    }

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
