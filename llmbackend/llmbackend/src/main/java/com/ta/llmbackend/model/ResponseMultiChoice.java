package com.ta.llmbackend.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Setter
@Getter
public class ResponseMultiChoice {

    private String id;

    private String question;

    private String answer;

    private String explanation;

}
