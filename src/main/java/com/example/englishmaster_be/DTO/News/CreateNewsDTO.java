package com.example.englishmaster_be.DTO.News;

import org.springframework.web.multipart.MultipartFile;

public class CreateNewsDTO {
    private String title;
    private String content;
    private MultipartFile image;

    public CreateNewsDTO() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public MultipartFile getImage() {
        return image;
    }

    public void setImage(MultipartFile image) {
        this.image = image;
    }
}
