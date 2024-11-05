package com.ta.llmbackend.service;

import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RabbitMQService {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private DirectExchange msgExchange;

    public String sendMsgRequest(String message) {

        // Request mesting message
        System.out.println(" [x] Requesting process(" + message + ")");

        // Send message to queue and receive the response as a byte array
        Object response = rabbitTemplate.convertSendAndReceive(msgExchange.getName(), "rpc", message);

        // Convert the response to a String if it's a byte array
        if (response instanceof byte[]) {
            return new String((byte[]) response); // Convert byte array to String
        } else if (response instanceof String) {
            return (String) response; // Cast to String directly
        } else {
            return "Invalid response type received";
        }

    }

}
