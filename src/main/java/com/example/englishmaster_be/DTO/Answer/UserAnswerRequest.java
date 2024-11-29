package com.example.englishmaster_be.DTO.Answer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserAnswerRequest {
    private UUID userId;
    private UUID questionId;
    private UUID answerId;
    private String content;
    private int position;
}
