package com.example.englishmaster_be.Model.Response;

import com.example.englishmaster_be.Model.DetailMockTest;

import java.util.UUID;

public class DetailMockTestResponse {
    private UUID answerId;
    private String answerContent;
    private boolean correctAnswer;
	private int scoreAnswer;

    public DetailMockTestResponse(DetailMockTest detailMockTest){
        this.answerId = detailMockTest.getDetailMockTestId();
        this.answerContent = detailMockTest.getAnswer().getAnswerContent();
        this.correctAnswer = detailMockTest.getAnswer().isCorrectAnswer();
		this.scoreAnswer = detailMockTest.getAnswer().getQuestion().getQuestionScore();
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

	public boolean isCorrectAnswer() {
		return correctAnswer;
	}

	public void setCorrectAnswer(boolean correctAnswer) {
		this.correctAnswer = correctAnswer;
	}

	public int getScoreAnswer() {
		return scoreAnswer;
	}

	public void setScoreAnswer(int scoreAnswer) {
		this.scoreAnswer = scoreAnswer;
	}
}
