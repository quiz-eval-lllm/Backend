package com.ta.llmbackend.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class Evaluation {

    private String id;

    private String question;

    private String context;

    private String explanation;

    private float score;

}
