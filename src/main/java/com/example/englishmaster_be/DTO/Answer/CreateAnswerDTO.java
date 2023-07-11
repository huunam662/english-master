package com.example.englishmaster_be.DTO.Answer;

import java.util.UUID;
public class CreateAnswerDTO {
    private String answerContent;
    private UUID questionId;
    private boolean correctAnswer;
    private String explainDetails;

    public CreateAnswerDTO() {
    }

    public String getAnswerContent() {
        return answerContent;
    }

    public void setAnswerContent(String answerContent) {
        this.answerContent = answerContent;
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
