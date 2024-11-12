package com.ta.llmbackend.dto;

import com.google.firebase.database.annotations.NotNull;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GenerateReq {

    @NotBlank
    private String userId;

    @NotBlank
    private String prompt;

    @NotBlank
    private String pdfUrl;

    @NotNull
    private int reqType;
}
