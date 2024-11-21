package com.ta.llmbackend.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ta.llmbackend.dto.request.AddQuestionReq;
import com.ta.llmbackend.dto.request.UpdateQuizQuestion;
import com.ta.llmbackend.exception.BadRequestException;
import com.ta.llmbackend.model.Package;
import com.ta.llmbackend.model.QuizEssay;
import com.ta.llmbackend.model.QuizMultiChoice;
import com.ta.llmbackend.repository.QuizEssayDb;
import com.ta.llmbackend.repository.QuizMultichoiceDb;

@Service
public class QuestionService {

    @Autowired
    private QuizEssayDb quizEssayDb;

    @Autowired
    private QuizMultichoiceDb quizMultichoiceDb;

    @Autowired
    private PackageService packageService;

    // Create Question
    public Object createQuestion(AddQuestionReq addQuestionReq) {

        Package quizPackage = packageService.getPackageById(UUID.fromString(addQuestionReq.getPackageId()));

        // Multichoice
        if (addQuestionReq.getType() == 0 && (addQuestionReq.getType() == quizPackage.getType())) {

            QuizMultiChoice quizMultiChoice = new QuizMultiChoice();
            quizMultiChoice.setPackageMultiChoice(quizPackage);
            quizMultiChoice.setAnswer(addQuestionReq.getAnswer());
            quizMultiChoice.setQuestion(addQuestionReq.getQuestion());
            quizMultiChoice.setExplanation(addQuestionReq.getExplanation());

            return quizMultichoiceDb.save(quizMultiChoice);

        } else if (addQuestionReq.getType() == 1 && (addQuestionReq.getType() == quizPackage.getType())) {

            QuizEssay quizEssay = new QuizEssay();
            quizEssay.setPackageEssay(quizPackage);
            quizEssay.setQuestion(addQuestionReq.getQuestion());
            quizEssay.setContext(addQuestionReq.getAnswer());

            return quizEssayDb.save(quizEssay);
        } else {
            throw new BadRequestException("Error when creating question");
        }
    }

    // Get all multichoice question
    public List<QuizMultiChoice> getAllMultiChoicesQuestionList() {

        return quizMultichoiceDb.findAll();
    }

    // Get all essay question
    public List<QuizEssay> getAllEssayQuestionList() {

        return quizEssayDb.findAll();
    }

    // Read multichoice by package id
    public List<QuizMultiChoice> questionPackageMultiChoice(UUID packageId) {

        List<QuizMultiChoice> questionList = new ArrayList<>();

        for (QuizMultiChoice quizMultiChoice : getAllMultiChoicesQuestionList()) {
            if (quizMultiChoice.getPackageMultiChoice().getPackageId().equals(packageId)) {
                questionList.add(quizMultiChoice);
            }
        }

        return questionList;
    }

    // Read essay by package id
    public List<QuizEssay> questionPackageEssay(UUID packageId) {

        List<QuizEssay> questionList = new ArrayList<>();

        for (QuizEssay quizEssay : getAllEssayQuestionList()) {
            if (quizEssay.getPackageEssay().getPackageId().equals(packageId)) {
                questionList.add(quizEssay);
            }
        }

        return questionList;
    }

    // Read multichoice by id
    public QuizMultiChoice getMultiChoiceById(UUID questionId) {

        for (QuizMultiChoice quizMultiChoice : getAllMultiChoicesQuestionList()) {
            if (quizMultiChoice.getMultichoiceId().equals(questionId)) {
                return quizMultiChoice;
            }
        }
        return null;
    }

    // Read essay by id
    public QuizEssay getEssayById(UUID questionId) {

        for (QuizEssay quizEssay : getAllEssayQuestionList()) {
            if (quizEssay.getEssayId().equals(questionId)) {
                return quizEssay;
            }
        }
        return null;
    }

    // Update question by id
    public Object updateQuizQuestion(UUID questionId, UpdateQuizQuestion updateQuizQuestion) {

        QuizMultiChoice quizMultiChoice = getMultiChoiceById(questionId);
        QuizEssay quizEssay = getEssayById(questionId);

        if (quizMultiChoice != null) {
            if (updateQuizQuestion.getQuestion() != "") {
                quizMultiChoice.setQuestion(updateQuizQuestion.getQuestion());
            }

            if (updateQuizQuestion.getAnswer() != "") {
                quizMultiChoice.setAnswer(updateQuizQuestion.getAnswer());
            }

            if (updateQuizQuestion.getExplanation() != "") {
                quizMultiChoice.setExplanation(updateQuizQuestion.getExplanation());
            }

            return quizMultichoiceDb.save(quizMultiChoice);
        } else if (quizEssay != null) {
            if (updateQuizQuestion.getQuestion() != "") {
                quizEssay.setQuestion(updateQuizQuestion.getQuestion());
            }

            if (updateQuizQuestion.getAnswer() != "") {
                quizEssay.setContext(updateQuizQuestion.getAnswer());
            }
            return quizEssayDb.save(quizEssay);
        } else {
            return null;
        }
    }

    // Delete multiple choice question by Id
    public void deleteQuestionMultipleChoice(UUID questionid) {

        QuizMultiChoice quizMultiChoice = getMultiChoiceById(questionid);

        quizMultichoiceDb.delete(quizMultiChoice);
    }

    // Delete essay question by Id
    public void deleteQuestionEssay(UUID questionid) {

        QuizEssay quizEssay = getEssayById(questionid);

        quizEssayDb.delete(quizEssay);
    }

}
