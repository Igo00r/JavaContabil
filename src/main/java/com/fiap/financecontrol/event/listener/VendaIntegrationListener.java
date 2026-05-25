package com.fiap.financecontrol.event.listener;

import com.fiap.financecontrol.event.events.VendaCriadaEvent;
import com.fiap.financecontrol.messaging.producer.VendaProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class VendaIntegrationListener {

    private final VendaProducer vendaProducer;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleVendaCriada(VendaCriadaEvent event) {
        vendaProducer.enviarMensagemVendaCriada(event.venda());
    }
}