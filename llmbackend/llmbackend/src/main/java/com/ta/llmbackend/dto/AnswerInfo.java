package com.ta.llmbackend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class AnswerInfo {

    @NotBlank
    private String questionId;

    @NotBlank
    private String answer;
}
