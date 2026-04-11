package com.fiap.financecontrol.services.usuario;

import com.fiap.financecontrol.domains.Usuario;
import com.fiap.financecontrol.repositories.UsuarioRepository;
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
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    @Transactional
    public void deleteUsuario(Long id) {
        if (!usuarioRepository.existsById(id)) {
            throw new RuntimeException("Usuario não encontrado com ID: " + id);
        }
        usuarioRepository.deleteById(id);
    }
    public Optional<Usuario> execute(Long id) {
        return usuarioRepository.findById(id);
    }

    public Usuario executeOrThrow(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario não encontrado com ID: " + id));
    }

    public Page<Usuario> listarUsuarios(int page, int size, Sort.Direction direction) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, "nome"));
        return usuarioRepository.findAll(pageable);
    }

    public Page<Usuario> listarUsuariosAtivos(int page, int size, Sort.Direction direction) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, "nome"));
        return usuarioRepository.findByAtivo("S", pageable);
    }

    public Page<Usuario> buscarUsuariosPorNome(String nome, int page, int size, Sort.Direction direction) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, "nome"));
        return usuarioRepository.findByNomeContainingAndAtivo(nome, "S", pageable);
    }


    @Transactional
    public Usuario updateUsuario(Usuario usuario) {
        if (!usuarioRepository.existsById(usuario.getId())) {
            throw new RuntimeException("Usuario não encontrado com ID: " + usuario.getId());
        }
        return usuarioRepository.save(usuario);
    }

    @Transactional
    public Usuario criarUsuario(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }






}
