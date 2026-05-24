package com.fiap.financecontrol.services.venda;

import com.fiap.financecontrol.presentation.dtos.request.VendaCreateDto;
import org.springframework.stereotype.Component;
public interface VendaValidator {
    void validar(VendaCreateDto dto, Long userId);
}

