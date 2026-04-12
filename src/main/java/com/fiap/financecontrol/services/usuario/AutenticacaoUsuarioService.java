package com.fiap.financecontrol.services.usuario;

import com.fiap.financecontrol.domains.Usuario;

import com.fiap.financecontrol.exceptions.SenhaInvalidaExeception;
import com.fiap.financecontrol.exceptions.UsuarioNaoEncontradoException;
import com.fiap.financecontrol.repositories.UsuarioRepository;
import com.fiap.financecontrol.presentation.dtos.request.LoginDataRequest;
import com.fiap.financecontrol.presentation.dtos.response.LoginDataResponse;
import com.fiap.financecontrol.security.TokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AutenticacaoUsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;


    public LoginDataResponse autenticar (LoginDataRequest loginDataRequest){
        Usuario user = usuarioRepository.findByEmail(loginDataRequest.email())
                .orElseThrow(() -> new UsuarioNaoEncontradoException("User nao encontrado"));

        log.info(loginDataRequest.toString());
        log.info(user.getEmail().toString());



        boolean senhaValida = passwordEncoder.matches(loginDataRequest.senha(),user.getPassword());

        if (!senhaValida) {
            throw new SenhaInvalidaExeception("Senha invalida");
        }

        String token = tokenService.gerarToken(user.getId(),user.getAuthorities());

        return new LoginDataResponse(token);

    }


}
