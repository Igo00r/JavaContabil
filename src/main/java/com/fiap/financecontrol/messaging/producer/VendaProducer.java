package com.fiap.financecontrol.messaging.producer;

import com.fiap.financecontrol.domains.Vendas;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class VendaProducer {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void enviarMensagemVendaCriada(Vendas venda) {

        rabbitTemplate.convertAndSend("fila-vendas", "Venda criada: " + venda.getId());
    }
}