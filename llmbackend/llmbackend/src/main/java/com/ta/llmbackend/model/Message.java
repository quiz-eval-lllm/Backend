package com.ta.llmbackend.model;

import java.util.List;
import java.util.UUID;

import com.google.cloud.Timestamp;
import com.google.cloud.firestore.annotation.DocumentId;
import com.google.cloud.firestore.annotation.ServerTimestamp;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Setter
@Getter
public class Message {

    @DocumentId
    private String requestId = UUID.randomUUID().toString();;

    private String userId;

    private String prompt;

    private String pdfUrl;

    private int reqType;

    private List<ResponseEssay> ResponseEssay;

    private List<ResponseMultiChoice> ResponseMultiChoice;

    private List<UserAns> userAns;

    private List<ExpectedAns> expectedAns;

    private List<Evaluation> evaluations;

    private float score;

    @ServerTimestamp
    private Timestamp timestamp;

}
