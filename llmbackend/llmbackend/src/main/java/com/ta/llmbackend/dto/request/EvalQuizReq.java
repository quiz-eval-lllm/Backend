package com.ta.llmbackend.dto.request;

import java.util.List;

import com.ta.llmbackend.dto.AnswerInfo;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class EvalQuizReq {

    private String quizId;

    @NotEmpty
    private List<AnswerInfo> answerInfo;

}
