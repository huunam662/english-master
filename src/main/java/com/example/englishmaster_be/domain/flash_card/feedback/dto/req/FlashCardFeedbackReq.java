package com.example.englishmaster_be.domain.flash_card.feedback.dto.req;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class FlashCardFeedbackReq {
    @Min(value = 1, message = "Star for feedback min 1.")
    @Max(value = 5, message = "Star for feedback max 5.")
    private Integer star;
    @NotNull(message = "Content for feedback not null.")
    @NotEmpty(message = "Content for feedback not empty.")
    @NotBlank(message = "Content for feedback not blank.")
    private String content;
}
