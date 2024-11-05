package com.ta.llmbackend.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Bean
    public Queue msgQueue() {
        return new Queue("msgQueue", false);
    }

    @Bean
    public DirectExchange msgExchange() {
        return new DirectExchange("msgExchange");
    }

    @Bean
    public Binding binding(Queue msgQueue, DirectExchange msgExchange) {
        return BindingBuilder.bind(msgQueue).to(msgExchange).with("rpc");
    }
}
