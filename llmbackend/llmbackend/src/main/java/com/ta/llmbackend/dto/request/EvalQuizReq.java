package com.ta.llmbackend.dto.request;

import java.util.List;

import com.ta.llmbackend.dto.AnswerInfo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class EvalQuizReq {

    @NotBlank
    private String quizId;

    @NotEmpty
    private List<AnswerInfo> answerInfo;

}
