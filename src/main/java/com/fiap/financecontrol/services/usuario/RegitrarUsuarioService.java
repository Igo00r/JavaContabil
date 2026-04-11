package com.fiap.financecontrol.services.usuario;

import com.fiap.financecontrol.domains.Usuario;
import com.fiap.financecontrol.repositories.UsuarioRepository;
import com.fiap.financecontrol.presentation.dtos.request.RegistroRequestDto;
import com.fiap.financecontrol.presentation.dtos.response.RegistroUsuarioResponse;
import com.fiap.financecontrol.security.PassEconderService;
import com.fiap.financecontrol.security.TokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class RegitrarUsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;
    private final PassEconderService passEconderService;

    public RegistroUsuarioResponse registrarUsuario(RegistroRequestDto registroRequestDto) {
        if (usuarioRepository.findByEmail(registroRequestDto.email()).isPresent()) {
            throw new IllegalArgumentException("Email já cadastrado");
        }
        String senhaHash = passEconderService.hash(registroRequestDto.senha());

        Usuario novoUsuario = Usuario.registrarUsuario(
                registroRequestDto.email(), registroRequestDto.cpfCnpj(), registroRequestDto.email(),senhaHash
        );
        Usuario usuarioRegistrado = usuarioRepository.save(novoUsuario);

        log.info("Usuario registrado: {}", usuarioRegistrado.getAuthorities());

        return new RegistroUsuarioResponse(
                usuarioRegistrado.getId(), usuarioRegistrado.getEmail(),usuarioRegistrado.getNome(),usuarioRegistrado.getDataCadastro()
        );
                
    }
}
