package com.example.englishmaster_be.DTO.FlashCard;

import org.springframework.web.multipart.MultipartFile;

public class CreateFlashCardDTO {
    private String flashCardTitle;

    private String flashCardDescription;

    private MultipartFile flashCardImage;

    public CreateFlashCardDTO() {
    }

    public String getFlashCardTitle() {
        return flashCardTitle;
    }

    public void setFlashCardTitle(String flashCardTitle) {
        this.flashCardTitle = flashCardTitle;
    }

    public String getFlashCardDescription() {
        return flashCardDescription;
    }

    public void setFlashCardDescription(String flashCardDescription) {
        this.flashCardDescription = flashCardDescription;
    }

    public MultipartFile getFlashCardImage() {
        return flashCardImage;
    }

    public void setFlashCardImage(MultipartFile flashCardImage) {
        this.flashCardImage = flashCardImage;
    }
}
