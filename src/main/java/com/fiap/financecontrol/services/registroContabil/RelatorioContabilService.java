package com.fiap.financecontrol.services.registroContabil;

import com.fiap.financecontrol.domains.CentroCusto;
import com.fiap.financecontrol.domains.RegistroContabil;
import com.fiap.financecontrol.domains.TipoConta;
import com.fiap.financecontrol.repositories.CentroCustoRepository;
import com.fiap.financecontrol.repositories.RegistroContabilRepository;
import com.fiap.financecontrol.presentation.dtos.RelatorioSaudeDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RelatorioContabilService {


    private final RegistroContabilRepository registroRepository;


    private final CentroCustoRepository centroCustoRepository;

    public RelatorioSaudeDto gerarRelatorio(Long centroCustoId, int mes, int ano) {

        CentroCusto cc = centroCustoRepository.findById(centroCustoId)
                .orElseThrow(() -> new RuntimeException("Centro de Custo não encontrado"));


        List<RegistroContabil> registros = registroRepository.findByCentroCustoAndPeriodo(centroCustoId, mes, ano);


        BigDecimal entradas = BigDecimal.ZERO;
        BigDecimal saídas = BigDecimal.ZERO;

        for (RegistroContabil reg : registros) {

            if (reg.getConta().getTipo() == TipoConta.R) {
                entradas = entradas.add(reg.getValor());
            } else {
                saídas = saídas.add(reg.getValor().abs()); // .abs() para somar o valor absoluto da saída
            }
        }

        BigDecimal saldoLiquido = entradas.subtract(saídas);

        // 4. Retornar o DTO consolidado
        return new RelatorioSaudeDto(
                cc.getNomeCentroCusto(), // Assumindo que tem getNome()
                entradas,
                saídas,
                saldoLiquido,
                mes,
                ano
        );
    }



}
