package com.fiap.financecontrol.services.usuario;


import com.fiap.financecontrol.domains.Usuario;
import com.fiap.financecontrol.repositories.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;


@Service
@RequiredArgsConstructor
public class ListUsuariosService {

    private final UsuarioRepository usuarioRepository;

    public Page<Usuario> listarUsuarios(int page, int size, Sort.Direction direction) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, "nomeCliente"));
        return usuarioRepository.findAll(pageable);
    }

    public Page<Usuario> lsitarUsuarioAtivos(int page, int size, Sort.Direction direction) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, "nomeCliente"));
        return usuarioRepository.findByAtivo("S", pageable);
    }

    public Page<Usuario> buscarUsuarioPorNome(String nome, int page, int size, Sort.Direction direction) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, "nomeCliente"));
        return usuarioRepository.findByNomeContainingAndAtivo(nome, "S", pageable);
    }


}
