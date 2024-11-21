package com.ta.llmbackend.dto.response;

import java.util.List;

import com.ta.llmbackend.dto.QuizDataInfo;
import com.ta.llmbackend.model.QuizMultiChoice;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class QuizMultiChoiceResponse {

    private QuizDataInfo quizDataInfo;

    private List<QuizMultiChoice> quizMultiChoice;
}
