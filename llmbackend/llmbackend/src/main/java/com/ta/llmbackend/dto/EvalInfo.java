package com.ta.llmbackend.dto;

import java.util.UUID;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class EvalInfo {

    private UUID questionId;

    private String question;

    private String userAnswer;

    private String expectedAnswer;

    private String explanation;

    private float score;

}
