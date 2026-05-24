package com.fiap.financecontrol.clients;

import com.fiap.financecontrol.presentation.dtos.response.ContaResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "conta-client", url = "http://localhost:8080")
public interface ContaClient {

    @GetMapping("/fiap/contas/{id}")
    ContaResponseDto buscarContaPorId(@PathVariable("id")Long id);
}