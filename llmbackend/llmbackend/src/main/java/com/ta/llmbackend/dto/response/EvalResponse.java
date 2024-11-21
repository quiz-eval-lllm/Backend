package com.ta.llmbackend.dto.response;

import java.util.List;
import java.util.UUID;

import com.ta.llmbackend.dto.EvalInfo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EvalResponse {

    private UUID quizId;

    private List<EvalInfo> evalInfoList;

    private float finalScore;
}
