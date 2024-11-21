package com.ta.llmbackend.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Setter
@Getter
public class StartQuizReq {

    @NotBlank
    private String userId;

    @NotBlank
    private String quizId;
}
