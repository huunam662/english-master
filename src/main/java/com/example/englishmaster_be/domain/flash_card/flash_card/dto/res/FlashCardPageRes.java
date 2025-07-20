package com.example.englishmaster_be.domain.flash_card.flash_card.dto.res;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class FlashCardPageRes {
    private FlashCardFullRes flashCard;
    private Long countFlashCardWords;
    private Long countFlashCardFeedbacks;
}
