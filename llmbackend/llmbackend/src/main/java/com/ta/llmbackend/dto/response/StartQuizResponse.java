package com.ta.llmbackend.dto.response;

import java.util.UUID;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Setter
@Getter
public class StartQuizResponse {

    private UUID userId;

    private UUID quizId;

    private UUID packageId;
}
