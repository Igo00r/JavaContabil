package com.fiap.financecontrol.services;

import com.fiap.financecontrol.domains.Usuario;
import com.fiap.financecontrol.gateways.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdateUsuarioService implements UsuarioDataServiceInterface {

    private final UsuarioRepository usuarioRepository;

    @Override
    @Transactional
    public Usuario execute(Usuario usuario) {
        if (!usuarioRepository.existsById(usuario.getId())) {
            throw new RuntimeException("Usuario não encontrado com ID: " + usuario.getId());
        }
        return usuarioRepository.save(usuario);
    }
}
