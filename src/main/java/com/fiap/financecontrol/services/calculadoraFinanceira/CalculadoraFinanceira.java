package com.fiap.financecontrol.services.calculadoraFinanceira;

import com.fiap.financecontrol.domains.RegistroContabil;

import java.math.BigDecimal;
import java.util.List;

public interface CalculadoraFinanceira {

    BigDecimal calcularEntradas(List<RegistroContabil> registros);

    BigDecimal calcularSaidas(List<RegistroContabil> registros);

    BigDecimal calcularSaldo(BigDecimal entradas, BigDecimal saidas);
}
