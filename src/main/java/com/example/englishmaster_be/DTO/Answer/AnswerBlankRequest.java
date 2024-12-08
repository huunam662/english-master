package com.example.englishmaster_be.DTO.Answer;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnswerBlankRequest {
    private String content;
    private int position;
    private UUID questionId;
}
