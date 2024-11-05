package com.ta.llmbackend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import com.ta.llmbackend.service.RabbitMQService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
public class LLMController {

    @Autowired
    private RabbitMQService rabbitMQService;

    @GetMapping("/send")
    public String sendRequest() {
        return rabbitMQService.sendMsgRequest("Hello%20Server!");
    }

}
