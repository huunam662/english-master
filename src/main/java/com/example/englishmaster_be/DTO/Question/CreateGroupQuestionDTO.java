package com.example.englishmaster_be.DTO.Question;

import java.util.UUID;


public class CreateGroupQuestionDTO {
    private String questionContent;
    private int questionScore;

    private int questionNumberical;
	private String questionExplainEn;
	private String questionExplainVn;

    private UUID questionGroupId;

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

	public int getQuestionNumberical() {
		return questionNumberical;
	}

	public void setQuestionNumberical(int questionNumberical) {
		this.questionNumberical = questionNumberical;
	}

	public UUID getQuestionGroupId() {
		return questionGroupId;
	}

	public void setQuestionGroupId(UUID questionGroupId) {
		this.questionGroupId = questionGroupId;
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
