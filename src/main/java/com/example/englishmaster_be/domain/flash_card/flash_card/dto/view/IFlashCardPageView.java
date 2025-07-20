package com.example.englishmaster_be.domain.flash_card.flash_card.dto.view;

import com.example.englishmaster_be.domain.flash_card.flash_card.model.FlashCardEntity;
import lombok.Data;
import lombok.NoArgsConstructor;

public interface IFlashCardPageView {

    FlashCardEntity getFlashCard();
    Long getCountFlashCardWords();
    Long getCountFlashCardFeedbacks();

    @Data
    @NoArgsConstructor
    class FlashCardPageView implements IFlashCardPageView {
        private FlashCardEntity flashCard;
        private Long countFlashCardWords;
        private Long countFlashCardFeedbacks;
    }
}
