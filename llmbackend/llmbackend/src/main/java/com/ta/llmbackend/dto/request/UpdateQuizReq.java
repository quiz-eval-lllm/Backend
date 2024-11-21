package com.ta.llmbackend.dto.request;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Setter
@Getter
public class UpdateQuizReq {

    private String title;

    private String category;

}
