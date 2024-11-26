package com.example.englishmaster_be.dto.answer;

import java.util.UUID;

public class CreateListAnswerDTO {
    private UUID idAnswer;
    private String contentAnswer;
    private boolean correctAnswer;

    public CreateListAnswerDTO(){

    }

    public UUID getIdAnswer() {
        return idAnswer;
    }

    public void setIdAnswer(UUID idAnswer) {
        this.idAnswer = idAnswer;
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
