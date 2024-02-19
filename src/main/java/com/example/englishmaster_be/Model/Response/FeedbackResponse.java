package com.example.englishmaster_be.Model.Response;

import com.example.englishmaster_be.Helper.GetExtension;
import com.example.englishmaster_be.Model.Feedback;
import com.example.englishmaster_be.Model.News;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.UUID;

public class FeedbackResponse {
    private UUID feedbackId;
    private String name;
    private String description;
    private String avatar;
    private String content;
    private boolean isEnable;
    private String createAt;
    private String updateAt;

    public FeedbackResponse(Feedback feedback) {
        String link = GetExtension.linkName(feedback.getAvatar());

        this.feedbackId = feedback.getFeedbackId();
        this.name = feedback.getName();
        this.content= feedback.getContent();
        this.description = feedback.getDescription();

        this.avatar =  link + feedback.getAvatar();
        this.isEnable = feedback.isEnable();

        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy hh:mm:ss");

        this.createAt = sdf.format(Timestamp.valueOf(feedback.getCreateAt()));
        this.updateAt = sdf.format(Timestamp.valueOf(feedback.getUpdateAt()));

    }

    public UUID getFeedbackId() {
        return feedbackId;
    }

    public void setFeedbackId(UUID feedbackId) {
        this.feedbackId = feedbackId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isEnable() {
        return isEnable;
    }

    public void setEnable(boolean enable) {
        isEnable = enable;
    }

    public String getCreateAt() {
        return createAt;
    }

    public void setCreateAt(String createAt) {
        this.createAt = createAt;
    }

    public String getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(String updateAt) {
        this.updateAt = updateAt;
    }
}
