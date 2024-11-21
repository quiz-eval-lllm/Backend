package com.ta.llmbackend.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ta.llmbackend.model.QuizActivities;
import com.ta.llmbackend.service.QuizService;
import com.ta.llmbackend.util.ResponseUtil;

@RestController
@RequestMapping("/api/v1/quiz")
public class QuizActivitiesController {

    @Autowired
    private QuizService quizService;

    // GET all quiz activites
    @GetMapping("/activity")
    public ResponseEntity<Object> getQuizActivity() {

        List<QuizActivities> listQuizActivities = quizService.getAllQuizActivities();

        if (listQuizActivities.isEmpty()) {
            return ResponseUtil.okResponse(null, "No quiz found");
        }

        return ResponseUtil.okResponse(listQuizActivities, "Successfully retrieve quiz");
    }

    // GET quiz from quiz_id
    @GetMapping("/activity/{id}")
    public ResponseEntity<Object> qetQuizActivityById(@PathVariable("id") String quizId) {

        QuizActivities quizActivities = quizService.getQuizActivitiesById(UUID.fromString(quizId));

        return ResponseUtil.okResponse(quizActivities, "Successfully retrieve quiz with id" + quizId);
    }

    // GET quiz assign to user
    @GetMapping("/activity/available/{id}")
    public ResponseEntity<Object> getQuizActivitiesByOwnership(@PathVariable("id") String userId,
            @RequestParam(value = "finished", required = false) Boolean isFinished) {

        List<QuizActivities> listQuizActivities = quizService.getQuizActivitiesByUser(UUID.fromString(userId));

        if (listQuizActivities != null) {
            if (isFinished != null) {
                listQuizActivities = quizService.getQuizActivitiesByFinished(listQuizActivities, isFinished);
            }

            return ResponseUtil.okResponse(listQuizActivities, "Successfully retrieve quiz");
        }

        return ResponseUtil.okResponse(null, "No quiz found");

    }

    // Start quiz
    @PostMapping("/start")
    public ResponseEntity<Object> startQuizActivity(@RequestParam(value = "user", required = true) String userId,
            @RequestParam(value = "quiz", required = true) String quizId) {

        QuizActivities quizActivities = quizService.getQuizActivitiesById(UUID.fromString(quizId));

        if (quizActivities != null) {
            if (quizActivities.getUser() != null
                    && quizActivities.getUser().getUserId().equals(UUID.fromString(userId))) {
                return ResponseUtil.okResponse(quizActivities, "Starting quiz");
            }
        }

        return ResponseUtil.okResponse(null, "Error starting quiz");
    }

    // Assign quiz to user
    @PostMapping("/assign")
    public ResponseEntity<Object> assignQuizActivity(@RequestParam(value = "user", required = true) String userId,
            @RequestParam(value = "package", required = true) String packageId) {

        QuizActivities quizActivities = quizService.createQuizActivity(UUID.fromString(userId),
                UUID.fromString(packageId));

        return ResponseUtil.okResponse(quizActivities, "Quiz with succecfully assigned");

    }

    // Restart quiz
    @PostMapping("/activity/{id}/restart")
    public ResponseEntity<Object> restartQuizActivity(@PathVariable("id") String quizId) {

        QuizActivities quizActivities = quizService.emptyQuizData(UUID.fromString(quizId));

        return ResponseUtil.okResponse(quizActivities, "Quiz with succecfully re-initiated");

    }

    // DELETE quiz activities
    @DeleteMapping("/activity/{id}/delete")
    public ResponseEntity<Object> deleteQuizActivitiesById(@PathVariable("id") String quizId) {

        quizService.deleteQuizActivities(UUID.fromString(quizId));

        return ResponseUtil.okResponse(null, "Quiz with id: " + quizId + " successfully deleted");
    }

}
