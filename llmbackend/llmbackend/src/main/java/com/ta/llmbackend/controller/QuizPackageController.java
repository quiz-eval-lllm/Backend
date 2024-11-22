package com.ta.llmbackend.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ta.llmbackend.service.PackageService;
import com.ta.llmbackend.service.QuestionService;
import com.ta.llmbackend.service.QuizService;
import com.ta.llmbackend.service.RabbitMQService;
import com.ta.llmbackend.util.ResponseUtil;
import com.ta.llmbackend.dto.request.GenerateQuizReq;
import com.ta.llmbackend.dto.request.UpdateQuizReq;
import com.ta.llmbackend.dto.response.GenerateQuizRPCResponse;
import com.ta.llmbackend.model.Package;
import com.ta.llmbackend.model.QuizActivities;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/quiz/package")
public class QuizPackageController {

    @Autowired
    private RabbitMQService rabbitMQService;

    @Autowired
    private PackageService packageService;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private QuizService quizService;

    // POST for generating quiz package
    @PostMapping("")
    public ResponseEntity<Object> generateQuiz(@Valid @RequestBody GenerateQuizReq request) throws IOException {

        Package quizPackage = packageService.createNewPackage(request);

        // Send message to rabbitMQ queue
        Map<String, Object> rabbitMQResponse = rabbitMQService.sendMsgForGenerate(quizPackage);
        String packageIdContent = (String) rabbitMQResponse.get("package_id");

        // Get question from packageIdContent
        // Map to the response {packageId, List<questionId>}
        // TODO: question list
        GenerateQuizRPCResponse response = new GenerateQuizRPCResponse();
        response.setPackageId(packageIdContent);

        return ResponseUtil.okResponse(response, "Successfully generating quiz package");
    }

    // GET quiz package
    @GetMapping("")
    public ResponseEntity<Object> getQuizPackage() {

        List<Package> packageList = packageService.getAllPackages();

        if (packageList.isEmpty()) {
            return ResponseUtil.okResponse(null, "No quiz package found");
        }

        return ResponseUtil.okResponse(packageList, "Successfully retrieve quiz package");
    }

    // GET quiz package from Id
    @GetMapping("/{id}")
    public ResponseEntity<Object> getQuizPackageById(@PathVariable("id") String packageId) {

        Package temp = packageService.getPackageById(UUID.fromString(packageId));

        return ResponseUtil.okResponse(temp, "Successfully retrieve quiz package with id" + temp.getPackageId());
    }

    // GET package containing question by Id
    @GetMapping("/{id}/question")
    public ResponseEntity<Object> getQuestionsPackage(@PathVariable("id") String packageId) {

        int questionType = packageService.getPackageById(UUID.fromString(packageId)).getType();

        List<?> questionPackageList = new ArrayList<>();

        // Multiple Choice
        if (questionType == 0) {
            questionPackageList = questionService.questionPackageMultiChoice(UUID.fromString(packageId));
        }

        // Essay
        if (questionType == 1) {
            questionPackageList = questionService.questionPackageEssay(UUID.fromString(packageId));
        }

        return ResponseUtil.okResponse(questionPackageList, "Successfully retrieve quiz questions");
    }

    // GET package assign to user
    @GetMapping("/available/{id}")
    public ResponseEntity<Object> getPackageByOwnership(@PathVariable("id") String userId,
            @RequestParam(value = "finished", required = false) Boolean isFinished) {

        List<Package> packageListSorted = new ArrayList<>();

        List<QuizActivities> listQuizActivities = quizService.getQuizActivitiesByUser(UUID.fromString(userId));

        if (listQuizActivities != null) {
            if (isFinished == null) {
                List<Package> packageList = packageService.getPackageFromQuizActivities(listQuizActivities);
                packageListSorted = packageService.sortPackageByCreateTime(packageList);
            } else {
                List<QuizActivities> listQuizActivitiesSorted = quizService
                        .getQuizActivitiesByFinished(listQuizActivities, isFinished);
                List<Package> packageList = packageService.getPackageFromQuizActivities(listQuizActivitiesSorted);
                packageListSorted = packageService.sortPackageByCreateTime(packageList);
            }

            return ResponseUtil.okResponse(packageListSorted, "Successfully retrieve package");

        }

        return ResponseUtil.okResponse(null, "No quiz package found");

    }

    // PUT package by Id
    @PutMapping("/{id}/update")
    public ResponseEntity<Object> updateQuizPackage(@RequestBody UpdateQuizReq quizQuizReq,
            @PathVariable("id") String packageId) {

        Package quizPackage = packageService.updatePackageById(UUID.fromString(packageId), quizQuizReq);

        return ResponseUtil.okResponse(quizPackage, "Quiz package with id: " + packageId + " successfully updated");

    }

    // DELETE package by id
    @DeleteMapping("/{id}/delete")
    public ResponseEntity<Object> deletePackageById(@PathVariable("id") String packageId) {

        packageService.deleteQuizPackage(UUID.fromString(packageId));

        return ResponseUtil.okResponse(null, "Quiz Package with id: " + packageId + " successfully deleted");
    }

}
