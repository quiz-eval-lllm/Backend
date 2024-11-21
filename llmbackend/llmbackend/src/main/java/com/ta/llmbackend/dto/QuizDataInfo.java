package com.ta.llmbackend.dto;

import java.util.UUID;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class QuizDataInfo {

    private UUID packageId;

    private String packageTitle;

    private String packageCategory;

    private int type;

    private UUID creatorId;
}
