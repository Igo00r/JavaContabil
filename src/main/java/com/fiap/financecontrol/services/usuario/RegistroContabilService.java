package com.fiap.financecontrol.services.usuario;

import com.fiap.financecontrol.domains.CentroCusto;
import com.fiap.financecontrol.domains.Conta;
import com.fiap.financecontrol.domains.RegistroContabil;
import com.fiap.financecontrol.repositories.RegistroContabilEventoDocument;
import com.fiap.financecontrol.repositories.RegistroContabilEventoMongoRepository;
import com.fiap.financecontrol.repositories.RegistroContabilRepository;
import com.fiap.financecontrol.services.registroContabil.RegistroContabilDataServiceInterface;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class RegistroContabilService  {

    private final RegistroContabilRepository registroContabilRepository;
    private final RegistroContabilEventoMongoRepository registroContabilEventoMongoRepository;


    public RegistroContabil execute(Conta conta, CentroCusto centroCusto, BigDecimal valor) {



        RegistroContabil registro = new RegistroContabil();
        registro.setValor(valor);
        registro.setConta(conta);
        registro.setCentroCusto(centroCusto);
        registro.setDataCriacao(LocalDateTime.now());
        registro.setDataAtualizacao(LocalDateTime.now());

        registro = registroContabilRepository.save(registro);


        RegistroContabilEventoDocument registroContabilEventoDocument =
                new RegistroContabilEventoDocument();

        registroContabilEventoDocument.setValor(registro.getValor());
        registroContabilEventoDocument.setContaId(registro.getConta().getId());
        registroContabilEventoDocument.setCentroCustoId(registro.getCentroCusto().getId());
        registroContabilEventoDocument.setDataCriacao(registro.getDataCriacao());
        registroContabilEventoDocument.setDataAtualizacao(registro.getDataAtualizacao());

        registroContabilEventoMongoRepository.save(registroContabilEventoDocument);

        return registroContabilRepository.save(registro);
    }
}