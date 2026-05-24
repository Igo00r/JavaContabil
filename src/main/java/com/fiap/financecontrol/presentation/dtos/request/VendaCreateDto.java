package com.fiap.financecontrol.presentation.dtos.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;


public record VendaCreateDto(



        @NotNull(message = "O ID da conta é obrigatório")
        Long contaId,

        @NotNull(message = "O ID do centro de custo é obrigatório")
        Long centroCustoId,

        @NotNull(message = "O valor total é obrigatório")
        @Positive(message = "O valor total deve ser maior que zero")
        BigDecimal valorTotal
) {}