package com.example.englishmaster_be.DTO.Question;

import java.util.UUID;

public class CreateQuestionDTO {

    private String questionContent;

    private int questionScore;

    private UUID partId;

	private String questionExplainEn;
	private String questionExplainVn;


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

	public String getQuestionExplainEn() {
		return questionExplainEn;
	}

	public void setQuestionExplainEn(String questionExplainEn) {
		this.questionExplainEn = questionExplainEn;
	}

	public String getQuestionExplainVn() {
		return questionExplainVn;
	}

	public void setQuestionExplainVn(String questionExplainVn) {
		this.questionExplainVn = questionExplainVn;
	}
}
