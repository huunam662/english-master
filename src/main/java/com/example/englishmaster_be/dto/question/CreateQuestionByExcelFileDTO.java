package com.example.englishmaster_be.dto.question;

import com.example.englishmaster_be.dto.answer.CreateListAnswerDTO;

import java.util.List;
import java.util.UUID;

public class CreateQuestionByExcelFileDTO {
    private UUID questionId;
    private String questionContent;

    private int questionScore;

    private String contentImage;

    private String contentAudio;

    private UUID partId;

    private List<CreateListAnswerDTO> listAnswer;

    private List<CreateQuestionByExcelFileDTO> listQuestionChild;

    private String questionExplainEn;
    private String questionExplainVn;


    public CreateQuestionByExcelFileDTO() {
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

    public String getContentImage() {
        return contentImage;
    }

    public void setContentImage(String contentImage) {
        this.contentImage = contentImage;
    }

    public String getContentAudio() {
        return contentAudio;
    }

    public void setContentAudio(String contentAudio) {
        this.contentAudio = contentAudio;
    }

    public List<CreateQuestionByExcelFileDTO> getListQuestionChild() {
        return listQuestionChild;
    }

    public void setListQuestionChild(List<CreateQuestionByExcelFileDTO> listQuestionChild) {
        this.listQuestionChild = listQuestionChild;
    }

    @Override
    public String toString() {
        return "CreateQuestionByExcelFileDTO{" +
                "questionId=" + questionId +
                ", questionContent='" + questionContent + '\'' +
                ", questionScore=" + questionScore +
                ", contentImage='" + contentImage + '\'' +
                ", contentAudio='" + contentAudio + '\'' +
                ", partId=" + partId +
                ", listAnswer=" + listAnswer +
                ", listQuestionChild=" + listQuestionChild +
                ", questionExplainEn='" + questionExplainEn + '\'' +
                ", questionExplainVn='" + questionExplainVn + '\'' +
                '}';
    }
}
