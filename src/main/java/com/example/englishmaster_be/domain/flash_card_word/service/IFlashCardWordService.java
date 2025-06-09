package com.example.englishmaster_be.domain.flash_card_word.service;

import com.example.englishmaster_be.domain.flash_card_word.dto.request.FlashCardWordRequest;
import com.example.englishmaster_be.domain.flash_card_word.model.FlashCardWordEntity;

import java.util.List;
import java.util.UUID;

public interface IFlashCardWordService {

    void delete(UUID flashCardWordId);

    List<String> searchByFlashCardWord(String keyWord);

    FlashCardWordEntity getFlashCardWordById(UUID flashCardWordId);

    FlashCardWordEntity saveFlashCardWord(FlashCardWordRequest flashCardWordRequest);

}

