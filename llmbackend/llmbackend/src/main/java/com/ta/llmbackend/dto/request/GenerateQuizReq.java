package com.ta.llmbackend.dto.request;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class GenerateQuizReq {

    @NotBlank
    private String userId;

    @NotBlank
    private String category;

    @NotBlank
    private String title;

    @NotNull
    private int type;

    @NotBlank
    private String prompt;

    private MultipartFile context;

}
