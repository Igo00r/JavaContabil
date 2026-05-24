package com.fiap.financecontrol.presentation;

import com.fiap.financecontrol.presentation.dtos.request.LoginDataRequest;
import com.fiap.financecontrol.presentation.dtos.response.LoginDataResponse;
import com.fiap.financecontrol.presentation.dtos.request.RegistroRequestDto;
import com.fiap.financecontrol.presentation.dtos.response.RegistroUsuarioResponse;
import com.fiap.financecontrol.services.autenticacaoService.AutenticacaoUsuarioService;
import com.fiap.financecontrol.services.usuario.RegitrarUsuarioService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

@RequestMapping("/fiap/autenticar")
@RestController
@RequiredArgsConstructor
public class AutenticacaoController {

    private final AutenticacaoUsuarioService autenticacaoUsuarioService;
    private final RegitrarUsuarioService regitrarUsuarioService;

    @PostMapping("/login")
    public ResponseEntity<LoginDataResponse> autenticar (
            @Valid @RequestBody LoginDataRequest loginDataRequest
    ){
        return ResponseEntity.ok(
                autenticacaoUsuarioService.autenticar(loginDataRequest)
        );

    }

    @PostMapping("/registrar")
    public ResponseEntity<RegistroUsuarioResponse> registrar (
            @Valid @RequestBody RegistroRequestDto dto,
            UriComponentsBuilder uriBuilder
    ){
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(regitrarUsuarioService.registrarUsuario(dto));
    }


}
