package com.example.englishmaster_be.domain.mock_test.evaluator_writing.dto;

public class WritingFeedbackResponse {
    private String feedbackEssay;

    public WritingFeedbackResponse() {}
    public WritingFeedbackResponse(String feedbackEssay) {
        this.feedbackEssay = feedbackEssay;
    }

    public String getFeedbackEssay() { return feedbackEssay; }
    public void setFeedbackEssay(String feedbackEssay) { this.feedbackEssay = feedbackEssay; }
}
