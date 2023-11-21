package com.example.englishmaster_be.DTO.Answer;

import java.util.UUID;

public class CreateListAnswerDTO {
    private String contentAnswer;
    private boolean correctAnswer;

    public CreateListAnswerDTO(){

    }

    public String getContentAnswer() {
        return contentAnswer;
    }

    public void setContentAnswer(String contentAnswer) {
        this.contentAnswer = contentAnswer;
    }

    public boolean isCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(boolean correctAnswer) {
        this.correctAnswer = correctAnswer;
    }
}
