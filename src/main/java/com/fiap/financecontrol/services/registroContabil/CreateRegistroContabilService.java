package com.fiap.financecontrol.services.registroContabil;

import com.fiap.financecontrol.domains.CentroCusto;
import com.fiap.financecontrol.domains.Conta;
import com.fiap.financecontrol.domains.RegistroContabil;
import com.fiap.financecontrol.repositories.CentroCustoRepository;
import com.fiap.financecontrol.repositories.ContaRepository;
import com.fiap.financecontrol.repositories.RegistroContabilRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CreateRegistroContabilService implements RegistroContabilDataServiceInterface {

    private final RegistroContabilRepository registroContabilRepository;
    private final ContaRepository contaRepository;
    private final CentroCustoRepository centroCustoRepository;


    @Override
    @Transactional
    public RegistroContabil execute(RegistroContabil registroContabil) {


        // Validar e carregar conta
        Conta conta = contaRepository.findById(registroContabil.getConta().getId())
                .orElseThrow(() -> new RuntimeException("Conta não encontrada com ID: " + registroContabil.getConta().getId()));
        registroContabil.setConta(conta);

        // Validar e carregar centro de custo
        CentroCusto centroCusto = centroCustoRepository.findById(registroContabil.getCentroCusto().getId())
                .orElseThrow(() -> new RuntimeException("Centro de Custo não encontrado com ID: " + registroContabil.getCentroCusto().getId()));
        registroContabil.setCentroCusto(centroCusto);

        return registroContabilRepository.save(registroContabil);
    }
}
