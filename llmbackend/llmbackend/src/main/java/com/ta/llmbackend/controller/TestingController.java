package com.ta.llmbackend.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ta.llmbackend.model.ExpectedAns;
import com.ta.llmbackend.model.Message;
import com.ta.llmbackend.model.UserAns;
import com.ta.llmbackend.service.FirebaseService;
import com.ta.llmbackend.service.MessageService;
import com.ta.llmbackend.service.RabbitMQService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
public class TestingController {

    @Autowired
    private RabbitMQService rabbitMQService;

    @Autowired
    private MessageService messageService;

    @Autowired
    private FirebaseService firebaseService;

    @PostMapping("/send")
    public ResponseEntity<String> sendRequest()
            throws ExecutionException, InterruptedException, JsonProcessingException {
        // Create message sample
        Message message = messageService.sendDummyTest("Testing");

        // Send data to Firestore
        String response = firebaseService.sendMessage(message);

        // Send message to RabbitMQ server
        // String rabbitMqResponse = rabbitMQService.sendMsgRequest(message);

        // Return the response
        return ResponseEntity.ok("Success uploading data to DB and message to RabbitMQ");
    }

    @GetMapping("/retrieve")
    public ResponseEntity<Message> getMessage(@RequestParam String userId)
            throws InterruptedException, ExecutionException {
        // Get message by userId
        Message message = firebaseService.getMessageFromUserId(userId);

        if (message != null) {
            return ResponseEntity.ok(message);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @GetMapping("/retrieve/expected-ans")
    public ResponseEntity<List<ExpectedAns>> getExpectedAnsFromMessage(@RequestParam String userId)
            throws InterruptedException, ExecutionException {
        // Get message by userId
        List<ExpectedAns> expectedAnsList = firebaseService.getExpectedAnsFromUserId(userId);

        if (expectedAnsList != null) {
            return ResponseEntity.ok(expectedAnsList);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @PutMapping("/message/update")
    public ResponseEntity<String> updateMessage(@RequestParam String userId, @RequestBody List<String> answerList)
            throws InterruptedException, ExecutionException {

        // Check if the answer list has exactly 5 items
        if (answerList.size() != 5) {
            return ResponseEntity.badRequest().body("Exactly 5 answers are required.");
        }

        // Create UserAns Object
        List<UserAns> userAnsList = new ArrayList<>();
        for (int i = 0; i < answerList.size(); i++) {
            UserAns userAns = new UserAns();
            userAns.setAnswer(answerList.get(i));
            userAnsList.add(userAns);
        }

        // Retrieve the existing ExpectedAns by userId
        List<ExpectedAns> expectedAnsList = firebaseService.getExpectedAnsFromUserId(userId);
        if (expectedAnsList == null) {
            return ResponseEntity.status(404).body("No document found for userId: " + userId);
        }

        // Update userAns field with new answers
        String updateResponse = firebaseService.updateAnsEssayFromUserId(userId, userAnsList, expectedAnsList);
        return ResponseEntity.ok(updateResponse);
    }

}
