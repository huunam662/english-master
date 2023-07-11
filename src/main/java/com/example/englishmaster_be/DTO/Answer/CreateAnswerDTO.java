package com.example.englishmaster_be.DTO.Answer;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreateAnswerDTO {
    private String answerContent;
    private UUID questionId;
    private boolean correctAnswer;
    private String explainDetails;

    public CreateAnswerDTO() {
    }
}
