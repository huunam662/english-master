package com.example.englishmaster_be.domain.flash_card.feedback.dto.view;

import com.example.englishmaster_be.domain.flash_card.feedback.model.FlashCardFeedbackEntity;
import lombok.Data;
import lombok.NoArgsConstructor;

public interface IFlashCardFeedbackPageView {

    FlashCardFeedbackEntity getFlashCardFeedback();

    @Data
    @NoArgsConstructor
    class FlashCardFeedbackPageView implements IFlashCardFeedbackPageView {
        private FlashCardFeedbackEntity flashCardFeedback;
    }

}
