package com.fiap.financecontrol.services;

import com.fiap.financecontrol.domains.Usuario;
import com.fiap.financecontrol.gateways.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ListClientesService {

    private final UsuarioRepository usuarioRepository;

    public Page<Usuario> listarClientes(int page, int size, Sort.Direction direction) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, "nome"));
        return usuarioRepository.findAll(pageable);
    }

    public Page<Usuario> listarClientesAtivos(int page, int size, Sort.Direction direction) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, "nome"));
        return usuarioRepository.findByAtivo("S", pageable);
    }

    public Page<Usuario> buscarClientesPorNome(String nome, int page, int size, Sort.Direction direction) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, "nome"));
        return usuarioRepository.findByNomeContainingAndAtivo(nome, "S", pageable);
    }
}
