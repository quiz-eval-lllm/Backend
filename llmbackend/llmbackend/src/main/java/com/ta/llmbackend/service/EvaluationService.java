package com.ta.llmbackend.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.aspectj.weaver.patterns.TypePatternQuestions.Question;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ta.llmbackend.dto.AnswerInfo;
import com.ta.llmbackend.dto.EvalInfo;
import com.ta.llmbackend.dto.request.EvalQuizReq;
import com.ta.llmbackend.dto.response.EvalResponse;
import com.ta.llmbackend.model.Evaluation;
import com.ta.llmbackend.model.QuizActivities;
import com.ta.llmbackend.model.QuizEssay;
import com.ta.llmbackend.model.QuizMultiChoice;
import com.ta.llmbackend.repository.EvaluationDb;
import com.ta.llmbackend.repository.QuizActivitiesDb;

@Service
public class EvaluationService {

    @Autowired
    private EvaluationDb evaluationDb;

    @Autowired
    private QuizActivitiesDb quizActivitiesDb;

    @Autowired
    private QuizService quizService;

    @Autowired
    private QuestionService questionService;

    // Create evaluation from question
    public List<Evaluation> createEvaluation(EvalQuizReq evalQuizReq) {

        List<Evaluation> evalList = new ArrayList<>();

        for (AnswerInfo answerInfo : evalQuizReq.getAnswerInfo()) {

            Evaluation evaluation = new Evaluation();

            evaluation.setQuestionId(UUID.fromString(answerInfo.getQuestionId()));

            if (evalQuizReq.getQuizId() != null && !evalQuizReq.getQuizId().isEmpty()) {
                evaluation
                        .setQuizActivities(quizService.getQuizActivitiesById(UUID.fromString(evalQuizReq.getQuizId())));
            } else {
                evaluation
                        .setQuizActivities(null);
            }

            evaluation.setUserAnswer(answerInfo.getAnswer());

            Evaluation temp = evaluationDb.save(evaluation);

            evalList.add(temp);
        }

        if (evalQuizReq.getQuizId() != null && !evalQuizReq.getQuizId().isEmpty()) {
            quizService.finishedQuizActivity(UUID.fromString(evalQuizReq.getQuizId()));
        }

        return evalList;
    }

    // Evaluation for each question & answer multiple choice
    public EvalResponse calculateMultiChoice(UUID quizId, List<Evaluation> evalList) {

        QuizActivities quiz = quizService.getQuizActivitiesById(quizId);

        float totalScore = 100;

        List<EvalInfo> evalInfoList = new ArrayList<>();

        for (Evaluation evaluation : evalList) {

            float score;

            String expectedAnswer = questionService.getMultiChoiceById(evaluation.getQuestionId()).getAnswer();
            if (!(evaluation.getUserAnswer().toLowerCase().equals(expectedAnswer.toLowerCase()))) {
                totalScore = totalScore - 20;
                score = 0;
            } else {
                score = 100;
            }

            EvalInfo evalInfo = new EvalInfo();
            QuizMultiChoice quizMultiChoice = questionService.getMultiChoiceById(evaluation.getQuestionId());

            evalInfo.setQuestionId(evaluation.getQuestionId());
            evalInfo.setQuestion(questionService.getMultiChoiceById(evaluation.getQuestionId()).getQuestion());
            evalInfo.setUserAnswer(evaluation.getUserAnswer());
            evalInfo.setExpectedAnswer(quizMultiChoice.getAnswer());
            evalInfo.setExplanation(quizMultiChoice.getExplanation());
            evalInfo.setScore(score);

            evalInfoList.add(evalInfo);

            evaluation.setScore(score);
            evaluationDb.save(evaluation);

            quiz.setFinalScore(totalScore);
            quizActivitiesDb.save(quiz);
        }

        return new EvalResponse(quizId, evalInfoList, totalScore);

    }

    // Read all evaluations
    public List<Evaluation> getAllEvaluation() {

        return evaluationDb.findAll();
    }

    // Read evaluation info by Id
    public EvalResponse getEvaluationById(UUID evalId) {

        for (Evaluation evaluation : getAllEvaluation()) {
            if (evaluation.getEvalId().equals(evalId)) {

                String question;
                String userAnswer;
                String expectedAnswer;
                String explanation;
                float score;

                // Getting question data
                // Check if it's multiple choice
                QuizMultiChoice quizMultiChoice = questionService.getMultiChoiceById(evaluation.getQuestionId());
                if (quizMultiChoice != null) {
                    question = quizMultiChoice.getQuestion();
                    userAnswer = evaluation.getUserAnswer();
                    expectedAnswer = quizMultiChoice.getAnswer();
                    explanation = quizMultiChoice.getAnswer();
                    score = evaluation.getScore();
                } else {
                    QuizEssay quizEssay = questionService.getEssayById(evaluation.getQuestionId());
                    question = quizEssay.getQuestion();
                    userAnswer = evaluation.getUserAnswer();
                    expectedAnswer = quizEssay.getContext();
                    explanation = "";
                    score = evaluation.getScore();
                }

                // Mapping eval info
                EvalInfo evalInfo = new EvalInfo();
                evalInfo.setQuestionId(evaluation.getQuestionId());
                evalInfo.setQuestion(question);
                evalInfo.setUserAnswer(userAnswer);
                evalInfo.setExpectedAnswer(expectedAnswer);
                evalInfo.setExplanation(explanation);
                evalInfo.setScore(score);

                // Mapping eval response
                EvalResponse evalResponse = new EvalResponse();
                evalResponse.setQuizId(evalId);

                List<EvalInfo> evalInfoList = new ArrayList<>();
                evalInfoList.add(evalInfo);
                evalResponse.setEvalInfoList(evalInfoList);

                return evalResponse;
            }
        }
        return null;
    }

    // Read evaluation by quiz activities
    public EvalResponse getEvaluationByQuiz(UUID quizId) {

        QuizActivities quizActivities = quizService.getQuizActivitiesById(quizId);

        EvalResponse eval = new EvalResponse();

        if (quizActivities != null) {
            List<Evaluation> evalList = quizActivities.getEvaluationList();

            if (!evalList.isEmpty()) {
                List<EvalInfo> evalInfoList = new ArrayList<>();

                for (Evaluation item : evalList) {
                    EvalResponse evalResponse = getEvaluationById(item.getEvalId());

                    evalInfoList.add(evalResponse.getEvalInfoList().get(0));

                }
                eval.setEvalInfoList(evalInfoList);
                eval.setQuizId(quizId);
                eval.setFinalScore(quizActivities.getFinalScore());

                return eval;
            }
        }
        return null;

    }

}
