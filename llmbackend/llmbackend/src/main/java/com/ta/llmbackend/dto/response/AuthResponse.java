package com.ta.llmbackend.dto.response;

import com.ta.llmbackend.model.Users;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class AuthResponse {

    private String token;

    private Users userInfo;
}
