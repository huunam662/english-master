package com.example.englishmaster_be.DTO.Question;

import java.util.UUID;

public class CreateQuestionDTO {

    private String questionContent;

    private int questionScore;

    private UUID partId;


	public CreateQuestionDTO() {
		super();
	}

	public String getQuestionContent() {
		return questionContent;
	}

	public void setQuestionContent(String questionContent) {
		this.questionContent = questionContent;
	}

	public int getQuestionScore() {
		return questionScore;
	}

	public void setQuestionScore(int questionScore) {
		this.questionScore = questionScore;
	}

	public UUID getPartId() {
		return partId;
	}

	public void setPartId(UUID partId) {
		this.partId = partId;
	}

    
}
