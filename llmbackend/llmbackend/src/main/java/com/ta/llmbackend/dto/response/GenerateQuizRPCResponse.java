package com.ta.llmbackend.dto.response;

import java.util.List;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class GenerateQuizRPCResponse {

    private String packageId;

    private List<String> questionId;
}
