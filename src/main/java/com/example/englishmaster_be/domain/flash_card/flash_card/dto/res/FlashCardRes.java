package com.example.englishmaster_be.domain.flash_card.flash_card.dto.res;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
public class FlashCardRes {
    private UUID id;
    private String title;
    private String image;
    private String description;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;
}
