package com.example.englishmaster_be.Model.Response;

import com.example.englishmaster_be.Model.Content;

import java.util.UUID;

public class ContentResponse {
    private UUID contentId;
    private String contentType;
    private String contentData;
    private UUID topicId;
    private String code;
    private UUID questionId;

    public ContentResponse(Content content) {
        this.contentId = content.getContentId();
        this.contentType = content.getContentType();
        this.contentData = content.getContentData();
        this.topicId = content.getTopicId();
        this.code = content.getCode();
        this.questionId = content.getQuestion() != null ? content.getQuestion().getQuestionId() : null;
    }

    public UUID getContentId() {
        return contentId;
    }

    public void setContentId(UUID contentId) {
        this.contentId = contentId;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getContentData() {
        return contentData;
    }

    public void setContentData(String contentData) {
        this.contentData = contentData;
    }

    public UUID getTopicId() {
        return topicId;
    }

    public void setTopicId(UUID topicId) {
        this.topicId = topicId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public UUID getQuestionId() {
        return questionId;
    }

    public void setQuestionId(UUID questionId) {
        this.questionId = questionId;
    }
}
