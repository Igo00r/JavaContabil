package com.fiap.financecontrol.services.calculadoraFinanceira;

import com.fiap.financecontrol.domains.RegistroContabil;
import com.fiap.financecontrol.domains.TipoConta;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class CalculadoraFinanceiraImpl implements CalculadoraFinanceira {

    @Override
    public BigDecimal calcularEntradas(List<RegistroContabil> registros) {

        return registros.stream()
                .filter(r -> r.getConta().getTipo() == TipoConta.R)
                .map(RegistroContabil::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public BigDecimal calcularSaidas(List<RegistroContabil> registros) {

        return registros.stream()
                .filter(r -> r.getConta().getTipo() != TipoConta.R)
                .map(r -> r.getValor().abs())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public BigDecimal calcularSaldo(BigDecimal entradas, BigDecimal saidas) {

        return entradas.subtract(saidas);
    }
}
