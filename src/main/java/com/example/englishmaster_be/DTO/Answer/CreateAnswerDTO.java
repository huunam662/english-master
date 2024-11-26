package com.example.englishmaster_be.DTO.Answer;

import java.util.UUID;
public class CreateAnswerDTO {
    private String contentAnswer;
    private UUID questionId;
    private boolean correctAnswer;
    private String explainDetails;

    public CreateAnswerDTO() {
    }

    public String getContentAnswer() {
        return contentAnswer;
    }

    public void setContentAnswer(String contentAnswer) {
        this.contentAnswer = contentAnswer;
    }

    public UUID getQuestionId() {
        return questionId;
    }

    public void setQuestionId(UUID questionId) {
        this.questionId = questionId;
    }

    public boolean isCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(boolean correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public String getExplainDetails() {
        return explainDetails;
    }

    public void setExplainDetails(String explainDetails) {
        this.explainDetails = explainDetails;
    }
}
