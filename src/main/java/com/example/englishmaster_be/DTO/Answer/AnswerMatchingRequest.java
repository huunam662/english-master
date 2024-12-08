package com.example.englishmaster_be.DTO.Answer;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnswerMatchingRequest {
    private String contentLeft;
    private String contentRight;
    private UUID questionId;
}
