package com.example.englishmaster_be.DTO.Answer;

import java.util.UUID;

public class UpdateAnswerDTO {
    private UUID answerId;
    private String answerContent;
    private UUID questionId;
    private boolean correctAnswer;
    private String explainDetails;

    public UpdateAnswerDTO() {
    }

    public UUID getAnswerId() {
        return answerId;
    }

    public void setAnswerId(UUID answerId) {
        this.answerId = answerId;
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
