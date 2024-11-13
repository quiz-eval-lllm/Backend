package com.ta.llmbackend.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ta.llmbackend.dto.GenerateReq;
import com.ta.llmbackend.model.Evaluation;
import com.ta.llmbackend.model.ExpectedAns;
import com.ta.llmbackend.model.Message;
import com.ta.llmbackend.model.ResponseEssay;
import com.ta.llmbackend.model.ResponseMultiChoice;
import com.ta.llmbackend.model.UserAns;

@Service
public class MessageService {

    @Autowired
    FirebaseService firebaseService;

    // Creating new instance
    public Message createMessage(GenerateReq request) {
        Message message = new Message();

        message.setUserId(request.getUserId());
        message.setPrompt(request.getPrompt());
        message.setPdfUrl(request.getPdfUrl());
        message.setReqType(request.getReqType());

        return message;

    }

    // Get all instance
    public List<Message> getAllMessage() throws InterruptedException, ExecutionException {
        return firebaseService.getAllMessages();
    }

    // Get instance by userId
    public Message getMessage(String userId) throws InterruptedException, ExecutionException {
        return firebaseService.getMessageFromUserId(userId);
    }

    // Get multichoice ans by message
    public List<String> getAnsMultichoice(String userId) throws InterruptedException, ExecutionException {
        Message message = getMessage(userId);
        List<String> ansList = new ArrayList<>();

        for (ResponseMultiChoice item : message.getResponseMultiChoice()) {
            ansList.add(item.getAnswer());
        }

        return ansList;
    }

    // Get essay context by userId
    public List<ExpectedAns> getExpectedAns(String userId) throws InterruptedException, ExecutionException {
        return firebaseService.getExpectedAnsFromUserId(userId);
    }

    // Calculate score and store evaluation to firestore
    public float calculateMultichoice(String userId, List<String> userAnsList, List<String> expectedAnsList)
            throws InterruptedException, ExecutionException {
        float score = 100;

        Message message = getMessage(userId);
        List<Evaluation> evalList = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            boolean flag = true;
            if (!(userAnsList.get(i).toLowerCase().equals(expectedAnsList.get(i).toLowerCase()))) {
                score = score - 20;
                flag = false;
            } else {
                score = score;
            }

            Evaluation eval = new Evaluation();
            eval.setId(message.getResponseMultiChoice().get(i).getId());
            eval.setQuestion(message.getResponseMultiChoice().get(i).getQuestion());
            eval.setUserAns(userAnsList.get(i));
            eval.setExpectedAns(message.getResponseMultiChoice().get(i).getAnswer());
            eval.setExplanation(message.getResponseMultiChoice().get(i).getExplanation());
            eval.setScore(flag);

            evalList.add(eval);
        }

        firebaseService.updateEvaluationFromUserId(userId, evalList);

        return score;
    }

    // Update UserAns and final score for multichoice to firestore
    public String updateUserAnsAndScoreMultiscore(String userId, List<String> userAns, float score)
            throws InterruptedException, ExecutionException {
        List<UserAns> userAnsList = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            UserAns temp = new UserAns();
            temp.setAnswer(userAns.get(i));
            userAnsList.add(temp);
        }
        return firebaseService.updateAnsAndScoreMultiscoreFromUserId(userId, userAnsList, score);
    }

    // Update UserAns and ExptectedAns for essay to firestore
    public String updateUserAnsAndExpectedAnsEssay(String userId, List<String> userAns, List<ExpectedAns> expectedAns)
            throws InterruptedException, ExecutionException {
        List<UserAns> userAnsList = new ArrayList<>();

        for (String item : userAns) {
            UserAns temp = new UserAns();
            temp.setAnswer(item);
            userAnsList.add(temp);
        }

        return firebaseService.updateAnsEssayFromUserId(userId, userAnsList, expectedAns);
    }

    public Message sendDummyTest(String prompt) {
        Message message = new Message();

        //// Dummy Payload
        // Response Essay
        List<ResponseEssay> responseEssayList = new ArrayList<>();
        ResponseEssay responseEssay = new ResponseEssay();
        for (int i = 1; i <= 5; i++) {
            responseEssay.setId(UUID.randomUUID().toString());
            responseEssay.setQuestion("Testing");
            responseEssay.setContext("Testing context");
            responseEssayList.add(responseEssay);
        }

        // Response Essay
        List<ResponseMultiChoice> ResponseMultiChoiceList = new ArrayList<>();
        ResponseMultiChoice responseMultiChoice = new ResponseMultiChoice();
        for (int i = 1; i <= 5; i++) {
            responseMultiChoice.setId(UUID.randomUUID().toString());
            responseMultiChoice.setQuestion("Testing");
            responseMultiChoice.setAnswer("A");
            responseMultiChoice.setExplanation("Testing Explain");
            ResponseMultiChoiceList.add(responseMultiChoice);
        }

        // User Answer
        List<UserAns> userAnsList = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            UserAns userAns = new UserAns();
            userAns.setAnswer("Answer");
            userAnsList.add(userAns);
        }

        // Expected Answer
        List<ExpectedAns> userExpectedList = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            ExpectedAns expectedAns = new ExpectedAns();
            expectedAns.setContext("A");
            userExpectedList.add(expectedAns);
        }

        message.setPrompt(prompt);
        message.setUserId(UUID.randomUUID().toString());
        message.setPdfUrl("www.zylker.com");
        message.setReqType(1);
        message.setResponseEssay(responseEssayList);
        // message.setResponseMultiChoice(ResponseMultiChoiceList);
        // message.setUserAns(userAnsList);
        message.setExpectedAns(userExpectedList);
        // message.setScore(10);
        return message;
    }

}
