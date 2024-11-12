package com.ta.llmbackend.util;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class BaseResponse {
    Integer code;
    String status;
    String message;
    Object content;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    List<String> errors;
}
