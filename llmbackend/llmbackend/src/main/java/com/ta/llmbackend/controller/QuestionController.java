package com.ta.llmbackend.controller;

import java.util.ArrayList;
import java.util.List;
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
import org.springframework.web.bind.annotation.RestController;

import com.ta.llmbackend.dto.QuizDataInfo;
import com.ta.llmbackend.dto.request.AddQuestionReq;
import com.ta.llmbackend.dto.request.UpdateQuizQuestion;
import com.ta.llmbackend.dto.response.QuizEssayResponse;
import com.ta.llmbackend.dto.response.QuizMultiChoiceResponse;
import com.ta.llmbackend.model.QuizEssay;
import com.ta.llmbackend.model.QuizMultiChoice;
import com.ta.llmbackend.service.QuestionService;
import com.ta.llmbackend.util.ResponseUtil;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/quiz/question")
public class QuestionController {

    @Autowired
    private QuestionService questionService;

    // POST question with package Id
    @PostMapping("")
    public ResponseEntity<Object> createQuestion(@Valid @RequestBody AddQuestionReq questionReq) {

        var question = questionService.createQuestion(questionReq);

        return ResponseUtil.okResponse(question, "Successfully create question");
    }

    // GET question individually
    @GetMapping("/{id}")
    public ResponseEntity<Object> getQuestion(@PathVariable("id") String questionId) {

        QuizDataInfo quizDataInfo = new QuizDataInfo();

        // Multichoice
        QuizMultiChoice questionMultiChoice = questionService.getMultiChoiceById(UUID.fromString(questionId));

        if (questionMultiChoice != null) {

            // Response mapping
            QuizMultiChoiceResponse quizMultiChoiceResponse = new QuizMultiChoiceResponse();

            quizDataInfo.setPackageId(questionMultiChoice.getPackageMultiChoice().getPackageId());
            quizDataInfo.setPackageSubject(questionMultiChoice.getPackageMultiChoice().getSubject());
            quizDataInfo.setPackageModule(questionMultiChoice.getPackageMultiChoice().getModule());
            quizDataInfo.setType(0);
            quizDataInfo.setCreatorId(questionMultiChoice.getPackageMultiChoice().getCreator().getUserId());

            List<QuizMultiChoice> temp = new ArrayList<>();
            temp.add(questionMultiChoice);

            quizMultiChoiceResponse.setQuizMultiChoice(temp);
            quizMultiChoiceResponse.setQuizDataInfo(quizDataInfo);

            return ResponseUtil.okResponse(quizMultiChoiceResponse,
                    "Successfully retrieve questions with id: " + questionMultiChoice.getMultichoiceId());
        } else {
            // If multichoice is null, try fetching the essay question
            QuizEssay questionEssay = questionService.getEssayById(UUID.fromString(questionId));
            if (questionEssay != null) {

                // Response mapping
                QuizEssayResponse quizEssayResponse = new QuizEssayResponse();

                quizDataInfo.setPackageId(questionEssay.getPackageEssay().getPackageId());
                quizDataInfo.setPackageSubject(questionEssay.getPackageEssay().getSubject());
                quizDataInfo.setPackageModule(questionEssay.getPackageEssay().getModule());
                quizDataInfo.setType(1);
                quizDataInfo.setCreatorId(questionEssay.getPackageEssay().getCreator().getUserId());

                List<QuizEssay> temp = new ArrayList<>();
                temp.add(questionEssay);

                quizEssayResponse.setQuizEssay(temp);
                quizEssayResponse.setQuizDataInfo(quizDataInfo);

                return ResponseUtil.okResponse(quizEssayResponse,
                        "Successfully retrieve questions with id: " + questionEssay.getEssayId());
            } else {
                return ResponseUtil.okResponse(null, "No question found");

            }
        }

    }

    // PUT question by Id
    @PutMapping("/{id}/update")
    public ResponseEntity<Object> updateQuestionMultiChoice(@PathVariable("id") String questionId,
            @RequestBody UpdateQuizQuestion updateQuizQuestion) {

        var question = questionService.updateQuizQuestion(UUID.fromString(questionId), updateQuizQuestion);

        return ResponseUtil.okResponse(question, "Question with id: " + questionId + " successfully updated");

    }

    // Delete question
    @DeleteMapping("/{id}/delete")
    public ResponseEntity<Object> deleteQuestion(@PathVariable("id") String questionId) {

        QuizMultiChoice quizMultiChoice = questionService.getMultiChoiceById(UUID.fromString(questionId));

        if (quizMultiChoice != null) {
            questionService.deleteQuestionMultipleChoice(UUID.fromString(questionId));
        } else {
            questionService.deleteQuestionEssay(UUID.fromString(questionId));
        }

        return ResponseUtil.okResponse(null, "Question with id: " + questionId + " successfully deleted");
    }
}
