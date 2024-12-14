package com.example.englishmaster_be.service;

import com.example.englishmaster_be.model.request.FlashCard.FlashCardWordRequest;
import com.example.englishmaster_be.entity.FlashCardWordEntity;

import java.util.List;
import java.util.UUID;

public interface IFlashCardWordService {

    void delete(UUID flashCardWordId);

    List<String> searchByFlashCardWord(String keyWord);

    FlashCardWordEntity getFlashCardWordById(UUID flashCardWordId);

    FlashCardWordEntity saveFlashCardWord(FlashCardWordRequest flashCardWordRequest);

}

