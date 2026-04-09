package com.fiap.financecontrol.services;

import com.fiap.financecontrol.domains.Usuario;
import com.fiap.financecontrol.gateways.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CreateUsuarioService implements UsuarioDataServiceInterface {

    private final UsuarioRepository usuarioRepository;

    @Override
    @Transactional
    public Usuario execute(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }
}
