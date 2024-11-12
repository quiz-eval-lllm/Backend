package com.ta.llmbackend.controller;

import java.util.List;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ta.llmbackend.dto.GenerateReq;
import com.ta.llmbackend.model.ExpectedAns;
import com.ta.llmbackend.model.Message;
import com.ta.llmbackend.service.FirebaseService;
import com.ta.llmbackend.service.MessageService;
import com.ta.llmbackend.service.RabbitMQService;
import com.ta.llmbackend.util.ResponseUtil;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1")
public class MessageController {

    @Autowired
    RabbitMQService rabbitMQService;

    @Autowired
    FirebaseService firebaseService;

    @Autowired
    MessageService messageService;

    // POST for generating multiple choice
    @PostMapping("/multichoice/generate")
    public ResponseEntity<Object> generateMultichoice(@Valid @RequestBody GenerateReq request)
            throws ExecutionException, InterruptedException, JsonProcessingException {

        // Create message
        Message newMessage = messageService.createMessage(request);

        // Send message to db
        String responseDb = firebaseService.sendMessage(newMessage);

        // Send message to rabbitMQ
        String responseRabbitMQ = rabbitMQService.sendMsgRequest(newMessage);

        return ResponseUtil.okResponse(newMessage, "Success uploading data to DB and message to RabbitMQ");
    }

    // POST for generating essay
    @PostMapping("/essay/generate")
    public ResponseEntity<Object> generateEssay(@Valid @RequestBody GenerateReq request)
            throws ExecutionException, InterruptedException, JsonProcessingException {

        // Create message
        Message newMessage = messageService.createMessage(request);

        // Send message to db
        String responseDb = firebaseService.sendMessage(newMessage);

        // Send message to rabbitMQ
        String responseRabbitMQ = rabbitMQService.sendMsgRequest(newMessage);

        return ResponseUtil.okResponse(newMessage, "Success uploading data to DB and message to RabbitMQ");

    }

    // GET for generating multiple choice
    @GetMapping("/multichoice/{userId}")
    public ResponseEntity<Object> getMultichoice(@PathVariable("userId") String userId)
            throws InterruptedException, ExecutionException {

        // Get message and map payload
        Message message = messageService.getMessage(userId);

        return ResponseUtil.okResponse(message, "Found message with userId: " + message.getUserId());
    }

    // GET for generating essay
    @GetMapping("/essay/{userId}")
    public ResponseEntity<Object> getEssay(@PathVariable("userId") String userId)
            throws InterruptedException, ExecutionException {

        // Get message and map payload
        Message message = messageService.getMessage(userId);

        return ResponseUtil.okResponse(message, "Found message with userId: " + message.getUserId());
    }

    // POST for evaluating multiple choice
    @PostMapping("/multichoice/evaluate/{userId}")
    public ResponseEntity<Object> evaluateMultichoice(@PathVariable("userId") String userId,
            @RequestBody List<String> answerList) throws InterruptedException, ExecutionException {

        // Get answer from message
        List<String> ansMultichoiceList = messageService.getAnsMultichoice(userId);

        // Calculate
        float finalScore = messageService.calculateMultichoice(answerList, ansMultichoiceList);

        // Update Message
        String responseDb = messageService.updateAnsAndScore(userId, answerList, finalScore);

        return ResponseUtil.okResponse(finalScore, "Success calculating score");
    }

    // POST for evaluating essay
    @PostMapping("/essay/evaluate/{userId}")
    public ResponseEntity<Object> evaluateEssay(@PathVariable("userId") String userId,
            @RequestBody List<String> answerList) throws InterruptedException, ExecutionException {

        // Get konteks from message
        List<ExpectedAns> expectedAns = messageService.getExpectedAns(userId);

        // Send message to db
        String responseDb = messageService.updateUserAndExpectedAnsEssay(userId, answerList, expectedAns);

        // Send message to rabbitMQ
        Message message = messageService.getMessage(userId);
        String responseRabbitMQ = firebaseService.sendMessage(message);

        return ResponseUtil.okResponse(responseRabbitMQ, "Success calculating score");
    }

}
