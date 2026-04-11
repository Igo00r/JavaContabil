package com.fiap.financecontrol.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class VendaConsumer {

    @RabbitListener(queues = "fila-vendas")
    public void consumir(String mensagem) {
        log.info("Mensagem recebida: " + mensagem);
    }
}
