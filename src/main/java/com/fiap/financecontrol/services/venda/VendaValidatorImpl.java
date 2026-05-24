package com.fiap.financecontrol.services.venda;

import com.fiap.financecontrol.clients.ContaClient;
import com.fiap.financecontrol.clients.UsuarioClient;
import com.fiap.financecontrol.presentation.dtos.request.VendaCreateDto;
import com.fiap.financecontrol.presentation.dtos.response.UsuarioResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class VendaValidatorImpl implements VendaValidator {
    private final UsuarioClient usuarioClient;
    private final ContaClient contaClient;

    @Override
    public void validar(VendaCreateDto vendaDto, Long userId) {



        log.info("Chamando serviço de usuário via Feign - ID: {}", userId);

        UsuarioResponseDto usuarioResponse = usuarioClient.buscarClientePorId(userId);

        if (usuarioResponse == null) {
            log.error("Falha: Usuário não encontrado via Feign - ID: {}", userId);
            throw new RuntimeException("Usuário não encontrado via Feign");
        }


        log.info("Usuário validado com sucesso via Feign");
    }
}
