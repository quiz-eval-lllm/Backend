package com.ta.llmbackend.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ta.llmbackend.dto.request.EvalQuizReq;
import com.ta.llmbackend.dto.response.EvalResponse;
import com.ta.llmbackend.model.Evaluation;
import com.ta.llmbackend.service.EvaluationService;
import com.ta.llmbackend.service.RabbitMQService;
import com.ta.llmbackend.util.ResponseUtil;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/evaluation")
public class EvaluationController {

    @Autowired
    private EvaluationService evaluationService;

    @Autowired
    private RabbitMQService rabbitMQService;

    // POST evaluate multiple choice
    @PostMapping("/multichoice")
    public ResponseEntity<Object> evaluateMultipleChoice(@Valid @RequestBody EvalQuizReq evalQuizReq) {

        List<Evaluation> evalList = evaluationService.createEvaluation(evalQuizReq);

        EvalResponse evalResponse = evaluationService.calculateMultiChoice(UUID.fromString(evalQuizReq.getQuizId()),
                evalList);

        return ResponseUtil.okResponse(evalResponse, "Succesfully calculated quiz with id: " + evalQuizReq.getQuizId());
    }

    @PostMapping("/essay")
    public ResponseEntity<Object> evaluateEssay(@Valid @RequestBody EvalQuizReq evalQuizReq)
            throws JsonProcessingException {

        List<Evaluation> evalList = evaluationService.createEvaluation(evalQuizReq);

        List<UUID> evalIdList = new ArrayList<>();

        for (Evaluation evaluation : evalList) {
            evalIdList.add(evaluation.getEvalId());
        }

        // Send message to rabbitMQ queue
        Map<String, Object> rabbitMQResponse = rabbitMQService.sendMsgForEvaluateEssay(2,
                UUID.fromString(evalQuizReq.getQuizId()), evalIdList);

        return ResponseUtil.okResponse(rabbitMQResponse,
                "Successfully generating quiz activity with id: " + evalQuizReq.getQuizId());
    }

    // GET all evaluation
    @GetMapping("/quiz/{id}")
    public ResponseEntity<Object> getAllEvaluation(@PathVariable("id") String quizId) {

        EvalResponse evalResponse = evaluationService.getEvaluationByQuiz(UUID.fromString(quizId));

        if (evalResponse == null) {
            return ResponseUtil.okResponse(null, "No evaluation found");
        }

        return ResponseUtil.okResponse(evalResponse, "Successfully retrieve evaluation");
    }

    // GET evaluation by eval Id
    @GetMapping("/{id}")
    public ResponseEntity<Object> getEvaluationById(@PathVariable("id") String evalId) {

        EvalResponse evaluation = evaluationService.getEvaluationById(UUID.fromString(evalId));

        return ResponseUtil.okResponse(evaluation, "Evaluation with id: " + evalId + " successfully retrieved");
    }

}
