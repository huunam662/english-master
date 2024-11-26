package com.example.englishmaster_be.dto.question;

import com.example.englishmaster_be.dto.answer.CreateListAnswerDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public class CreateQuestionDTO {

	private UUID questionId;
    private String questionContent;

    private int questionScore;

	private MultipartFile contentImage;

	private MultipartFile contentAudio;

    private UUID partId;

	private List<CreateListAnswerDTO> listAnswer;

	private List<CreateQuestionDTO> listQuestionChild;

	private String questionExplainEn;
	private String questionExplainVn;


	public CreateQuestionDTO() {
		super();
	}

	public UUID getQuestionId() {
		return questionId;
	}

	public void setQuestionId(UUID questionId) {
		this.questionId = questionId;
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

	public List<CreateListAnswerDTO> getListAnswer() {
		return listAnswer;
	}

	public void setListAnswer(List<CreateListAnswerDTO> listAnswer) {
		this.listAnswer = listAnswer;
	}

	public MultipartFile getContentImage() {
		return contentImage;
	}

	public void setContentImage(MultipartFile contentImage) {
		this.contentImage = contentImage;
	}

	public MultipartFile getContentAudio() {
		return contentAudio;
	}

	public void setContentAudio(MultipartFile contentAudio) {
		this.contentAudio = contentAudio;
	}

	public List<CreateQuestionDTO> getListQuestionChild() {
		return listQuestionChild;
	}

	public void setListQuestionChild(List<CreateQuestionDTO> listQuestionChild) {
		this.listQuestionChild = listQuestionChild;
	}
}
