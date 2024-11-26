package com.example.englishmaster_be.dto.feedback;

import org.springframework.web.multipart.MultipartFile;

public class CreateFeedbackDTO {
    private String name;
    private String description;
    private MultipartFile avatar;
    private String content;

    public CreateFeedbackDTO() {
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

    public MultipartFile getAvatar() {
        return avatar;
    }

    public void setAvatar(MultipartFile avatar) {
        this.avatar = avatar;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
