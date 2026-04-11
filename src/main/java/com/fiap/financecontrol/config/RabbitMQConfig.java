package com.fiap.financecontrol.config;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Bean
    public Queue filaVendas() {
        return new Queue("fila-vendas", true);
    }

}