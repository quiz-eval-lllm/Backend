package com.ta.llmbackend.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.amqp.rabbit.connection.ConnectionFactory;

@Configuration
public class RabbitMQConfig {

    // Configuration for Spring Boot <-> DGX

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

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setReplyTimeout(600_000);
        return rabbitTemplate;
    }

}
