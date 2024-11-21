package com.ta.llmbackend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Setter
@Getter
public class AddQuestionReq {

    @NotBlank
    private String packageId;

    @NotNull
    private int type;

    @NotBlank
    private String question;

    @NotBlank
    private String answer;

    private String explanation;
}
