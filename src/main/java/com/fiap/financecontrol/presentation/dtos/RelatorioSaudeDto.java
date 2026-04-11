package com.fiap.financecontrol.presentation.dtos;

import java.math.BigDecimal;

public record RelatorioSaudeDto(
        String nomeCentroCusto,
        BigDecimal totalEntradas,
        BigDecimal totalSaidas,
        BigDecimal saldoLiquido,
        Integer mes,
        Integer ano
) {}