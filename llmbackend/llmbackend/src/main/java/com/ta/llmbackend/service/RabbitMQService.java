package com.ta.llmbackend.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ta.llmbackend.model.Package;

@Service
public class RabbitMQService {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private DirectExchange msgExchange;

    @Autowired
    private ObjectMapper objectMapper;

    public String sendMsgRequest(Package packageMsg) throws JsonProcessingException {

        Map<String, Object> msgData = new HashMap<>();
        msgData.put("userId", packageMsg.getCreator().getUserId());
        msgData.put("reqType", packageMsg.getType());

        System.out.println(" [client] Requesting process(userId: " +
                packageMsg.getCreator().getUserId() + ", reqType: "
                + packageMsg.getType() + ")");

        // Send message to queue and receive the response as a byte array
        String messageToServer = objectMapper.writeValueAsString(msgData);
        Object response = rabbitTemplate.convertSendAndReceive(msgExchange.getName(),
                "rpc", messageToServer);

        // Convert the response to a String if it's a byte array
        if (response instanceof byte[]) {
            return new String((byte[]) response);
        } else if (response instanceof String) {
            return (String) response;
        } else {
            return "Invalid response type received";
        }

    }

}
