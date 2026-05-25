package com.fiap.financecontrol;

import com.fiap.financecontrol.domains.CentroCusto;
import com.fiap.financecontrol.domains.Conta;
import com.fiap.financecontrol.domains.RegistroContabil;
import com.fiap.financecontrol.domains.TipoConta;
import com.fiap.financecontrol.presentation.dtos.RelatorioSaudeDto;
import com.fiap.financecontrol.repositories.CentroCustoRepository;
import com.fiap.financecontrol.repositories.RegistroContabilRepository;
import com.fiap.financecontrol.services.calculadoraFinanceira.CalculadoraFinanceira;
import com.fiap.financecontrol.services.registroContabil.RelatorioContabilService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class RelatorioContabilServiceTest {

    @InjectMocks
    private RelatorioContabilService service;

    @Mock
    private RegistroContabilRepository registroRepository;

    @Mock
    private CentroCustoRepository centroCustoRepository;

    @Mock
    private CalculadoraFinanceira calculadoraFinanceira;

    @Test
    void deveGerarRelatorioCorretamente() {

        Long centroId = 1L;

        CentroCusto cc = new CentroCusto();
        cc.setId(centroId);
        cc.setNomeCentroCusto("Financeiro");

        Conta contaReceita = new Conta();
        contaReceita.setTipo(TipoConta.R);

        Conta contaDespesa = new Conta();
        contaDespesa.setTipo(TipoConta.D);

        RegistroContabil r1 = new RegistroContabil();
        r1.setConta(contaReceita);
        r1.setValor(new BigDecimal("100"));

        RegistroContabil r2 = new RegistroContabil();
        r2.setConta(contaDespesa);
        r2.setValor(new BigDecimal("50"));

        List<RegistroContabil> registros = List.of(r1, r2);

        when(centroCustoRepository.findById(centroId))
                .thenReturn(Optional.of(cc));

        when(registroRepository.findByCentroCustoAndPeriodo(
                centroId,
                1,
                2024
        )).thenReturn(registros);

        when(calculadoraFinanceira.calcularEntradas(anyList()))
                .thenReturn(new BigDecimal("100"));

        when(calculadoraFinanceira.calcularSaidas(anyList()))
                .thenReturn(new BigDecimal("50"));

        when(calculadoraFinanceira.calcularSaldo(
                new BigDecimal("100"),
                new BigDecimal("50")
        )).thenReturn(new BigDecimal("50"));

        // Act
        RelatorioSaudeDto dto =
                service.gerarRelatorio(centroId, 1, 2024);

        // Assert
        assertEquals(new BigDecimal("100"), dto.totalEntradas());
        assertEquals(new BigDecimal("50"), dto.totalSaidas());
        assertEquals(new BigDecimal("50"), dto.saldoLiquido());
        assertEquals("Financeiro", dto.nomeCentroCusto());
    }
}