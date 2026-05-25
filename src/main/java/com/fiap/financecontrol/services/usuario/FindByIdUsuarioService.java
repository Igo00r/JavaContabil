package com.fiap.financecontrol.services.usuario;

import com.fiap.financecontrol.domains.Usuario;
import com.fiap.financecontrol.exceptions.UsuarioNaoEncontradoException;
import com.fiap.financecontrol.repositories.UsuarioRepository;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FindByIdUsuarioService {

    private final UsuarioRepository usuarioRepository;
    public Optional<Usuario> execute(Long id) {
        return usuarioRepository.findById(id);
    }

    public Usuario executeOrThrow(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new UsuarioNaoEncontradoException("Usuario não encontrado com ID: " + id));
    }




}
