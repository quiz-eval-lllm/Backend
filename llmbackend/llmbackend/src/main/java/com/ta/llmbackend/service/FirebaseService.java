package com.ta.llmbackend.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.api.core.ApiFuture;
import com.ta.llmbackend.model.ExpectedAns;
import com.ta.llmbackend.model.Message;
import com.ta.llmbackend.model.UserAns;
import com.google.cloud.firestore.*;

@Service
public class FirebaseService {

    @Autowired
    private Firestore firestore;

    // Create Message
    public String sendMessage(Message message) throws ExecutionException, InterruptedException {
        ApiFuture<DocumentReference> data = firestore.collection("message").add(message);
        return "Saved data for id: " + message.getUserId() + ", with: " + data.get().getId();
    }

    // Get Message by UserId
    public Message getMessageFromUserId(String userId) throws InterruptedException, ExecutionException {
        Query query = firestore.collection("message").whereEqualTo("userId", userId);
        ApiFuture<QuerySnapshot> querySnapshot = query.get();

        List<Message> messageList = new ArrayList<>();

        for (QueryDocumentSnapshot document : querySnapshot.get().getDocuments()) {
            Message message = document.toObject(Message.class);
            messageList.add(message);
        }

        // Get last instance
        if (!messageList.isEmpty()) {
            return messageList.get(messageList.size() - 1);
        } else {
            return null;
        }

    }

    // Get ExpectedAns by UserId
    public List<ExpectedAns> getExpectedAnsFromUserId(String userId) throws InterruptedException, ExecutionException {
        Query query = firestore.collection("message").whereEqualTo("userId", userId);
        ApiFuture<QuerySnapshot> querySnapshot = query.get();

        if (!querySnapshot.get().isEmpty()) {
            DocumentSnapshot document = querySnapshot.get().getDocuments().get(0);
            Message message = document.toObject(Message.class);
            return message != null ? message.getExpectedAns() : null;
        }
        return null;
    }

    // Update Ans Message by UserId for Evaluating Essay
    public String updateAnsEssayFromUserId(String userId, List<UserAns> userAns, List<ExpectedAns> expectedAns)
            throws InterruptedException, ExecutionException {
        Query query = firestore.collection("message").whereEqualTo("userId", userId);
        ApiFuture<QuerySnapshot> querySnapshot = query.get();

        if (!querySnapshot.get().isEmpty()) {
            DocumentReference docRef = querySnapshot.get().getDocuments().get(0).getReference();
            ApiFuture<WriteResult> writeResult = docRef.update("userAns", userAns,
                    "reqType", 2);

            return "Updated userAns for userId: " + userId + " at " + writeResult.get().getUpdateTime();
        } else {
            return "Document with userId: " + userId + " not found.";
        }
    }

    // Update UserAns and Score by UserId
    public String updateAnsAndScoreFromUserId(String userId, List<UserAns> userAns, float score)
            throws InterruptedException, ExecutionException {
        Query query = firestore.collection("message").whereEqualTo("userId", userId);
        ApiFuture<QuerySnapshot> querySnapshot = query.get();

        if (!querySnapshot.get().isEmpty()) {
            DocumentReference docRef = querySnapshot.get().getDocuments().get(0).getReference();
            ApiFuture<WriteResult> writeResult = docRef.update("userAns", userAns, "score", score);

            return "Updated userAns for userId: " + userId + " at " + writeResult.get().getUpdateTime();
        } else {
            return "Document with userId: " + userId + " not found.";
        }
    }
}
