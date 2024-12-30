package com.ta.llmbackend.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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

    // Sending message for generating quiz
    @SuppressWarnings("unchecked")
    public Map<String, Object> sendMsgForGenerate(Package packageMsg) throws JsonProcessingException {

        Map<String, Object> msgData = new HashMap<>();
        msgData.put("packageId", packageMsg.getPackageId());
        msgData.put("reqType", packageMsg.getType());
        msgData.put("language", packageMsg.getLanguage());

        System.out.println(" [client] Requesting quiz generating process(package_id: " +
                packageMsg.getPackageId() + ", reqType: "
                + packageMsg.getType() + ")");

        String messageToServer = objectMapper.writeValueAsString(msgData);
        Object response = rabbitTemplate.convertSendAndReceive(msgExchange.getName(),
                "rpc", messageToServer);

        if (response instanceof byte[]) {
            String responseString = new String((byte[]) response);
            return objectMapper.readValue(responseString, Map.class);
        } else if (response instanceof String) {
            return objectMapper.readValue((String) response, Map.class);
        } else {
            throw new IllegalArgumentException("Invalid response type received");
        }

    }

    // Sending message for essay
    @SuppressWarnings("unchecked")
    public Map<String, Object> sendMsgForEvaluateEssay(int type, UUID quizId, List<UUID> evalIdList)
            throws JsonProcessingException {

        Map<String, Object> msgData = new HashMap<>();
        msgData.put("quizId", quizId);
        msgData.put("evalIdList", evalIdList);
        msgData.put("reqType", type);

        System.out.println(" [client] Requesting quiz evaluation process(quiz_id: " +
                quizId + ", reqType: "
                + type + ")");

        String messageToServer = objectMapper.writeValueAsString(msgData);
        Object response = rabbitTemplate.convertSendAndReceive(msgExchange.getName(),
                "rpc", messageToServer);

        // Convert the response to a map
        if (response instanceof byte[]) {
            String responseString = new String((byte[]) response);
            return objectMapper.readValue(responseString, Map.class);
        } else if (response instanceof String) {
            return objectMapper.readValue((String) response, Map.class);
        } else {
            throw new IllegalArgumentException("Invalid response type received");
        }
    }

    // TODO: Consume Requests in Spring Boot

}
