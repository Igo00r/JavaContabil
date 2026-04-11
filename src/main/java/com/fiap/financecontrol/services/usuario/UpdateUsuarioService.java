package com.fiap.financecontrol.services.usuario;

import com.fiap.financecontrol.domains.Usuario;
import com.fiap.financecontrol.repositories.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdateUsuarioService implements UsuarioDataServiceInterface{

    private final UsuarioRepository usuarioRepository;

    @Transactional
    public Usuario execute(Usuario cliente) {
        if (!usuarioRepository.existsById(cliente.getId())) {
            throw new RuntimeException("Cliente não encontrado com ID: " + cliente.getId());
        }
        return usuarioRepository.save(cliente);
    }


}
