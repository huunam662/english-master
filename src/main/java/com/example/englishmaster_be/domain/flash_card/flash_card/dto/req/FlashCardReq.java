package com.example.englishmaster_be.domain.flash_card.flash_card.dto.req;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class FlashCardReq {
    @NotNull(message = "Title is not null.")
    @NotEmpty(message = "Title is not empty.")
    @NotBlank(message = "Title is not blank.")
    private String title;
    private String image;
    private String description;
}
