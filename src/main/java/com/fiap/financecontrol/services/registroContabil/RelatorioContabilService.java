package com.fiap.financecontrol.services.registroContabil;

import com.fiap.financecontrol.domains.CentroCusto;
import com.fiap.financecontrol.domains.RegistroContabil;
import com.fiap.financecontrol.exceptions.CentroCustoNaoEncontradoException;
import com.fiap.financecontrol.repositories.CentroCustoRepository;
import com.fiap.financecontrol.repositories.RegistroContabilRepository;
import com.fiap.financecontrol.presentation.dtos.RelatorioSaudeDto;
import com.fiap.financecontrol.services.calculadoraFinanceira.CalculadoraFinanceira;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RelatorioContabilService {


    private final RegistroContabilRepository registroRepository;

    private final CentroCustoRepository centroCustoRepository;

    private final CalculadoraFinanceira calculadoraFinanceira;


    public RelatorioSaudeDto gerarRelatorio(Long centroCustoId, int mes, int ano) {

        CentroCusto cc = centroCustoRepository.findById(centroCustoId)
                .orElseThrow(() ->
                        new CentroCustoNaoEncontradoException(
                                "Centro de Custo não encontrado"
                        )
                );

        List<RegistroContabil> registros =
                registroRepository.findByCentroCustoAndPeriodo(
                        centroCustoId,
                        mes,
                        ano
                );

        BigDecimal entradas =
                calculadoraFinanceira.calcularEntradas(registros);

        BigDecimal saidas =
                calculadoraFinanceira.calcularSaidas(registros);

        BigDecimal saldo =
                calculadoraFinanceira.calcularSaldo(
                        entradas,
                        saidas
                );


        return new RelatorioSaudeDto(
                cc.getNomeCentroCusto(),
                entradas,
                saidas,
                saldo,
                mes,
                ano
        );
    }



}
