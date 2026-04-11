package com.fiap.financecontrol.clients;

import com.fiap.financecontrol.presentation.dtos.response.UsuarioResponseDto;
import com.fiap.financecontrol.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "cliente-client", url = "http://localhost:8080",configuration = FeignConfig.class)
public interface UsuarioClient {

    @GetMapping("/fiap/usuarios/{id}")
    UsuarioResponseDto buscarClientePorId(@PathVariable Long id);
}