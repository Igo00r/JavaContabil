package com.fiap.financecontrol.services;

import com.fiap.financecontrol.domains.Usuario;
import com.fiap.financecontrol.gateways.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FindByIdClienteService {

    private final UsuarioRepository usuarioRepository;

    public Optional<Usuario> execute(Long id) {
        return usuarioRepository.findById(id);
    }

    public Usuario executeOrThrow(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario não encontrado com ID: " + id));
    }
}
