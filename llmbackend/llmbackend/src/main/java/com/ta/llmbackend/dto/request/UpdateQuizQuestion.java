package com.ta.llmbackend.dto.request;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Setter
@Getter
public class UpdateQuizQuestion {

    private String question;

    private String answer;

    private String explanation;

}
