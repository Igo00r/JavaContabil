package com.fiap.financecontrol.services.usuario;



import com.fiap.financecontrol.repositories.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DeleteUsuarioService {

    private final UsuarioRepository usuarioRepository;

    @Transactional
    public void execute(Long id) {
        if (!usuarioRepository.existsById(id)) {
            throw new RuntimeException("Cliente não encontrado com ID: " + id);
        }
        usuarioRepository.deleteById(id);
    }
}
