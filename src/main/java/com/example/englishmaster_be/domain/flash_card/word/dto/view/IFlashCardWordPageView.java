package com.example.englishmaster_be.domain.flash_card.word.dto.view;

import com.example.englishmaster_be.domain.flash_card.word.model.FlashCardWordEntity;
import lombok.Data;
import lombok.NoArgsConstructor;

public interface IFlashCardWordPageView {

    FlashCardWordEntity getFlashCardWord();

    @Data
    @NoArgsConstructor
    class FlashCardWordPageView implements IFlashCardWordPageView{
        private FlashCardWordEntity flashCardWord;
    }

}
