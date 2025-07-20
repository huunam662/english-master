package com.example.englishmaster_be.domain.flash_card.word.dto.req;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class FlashCardWordReq {
    @NotNull(message = "Word of flash card word is not null.")
    @NotEmpty(message = "Word of flash card word is not empty.")
    @NotBlank(message = "Word of flash card word is not blank.")
    private String word;
    private String meaning;
    private String image;
    private String wordType;
    private String pronunciation;
}
