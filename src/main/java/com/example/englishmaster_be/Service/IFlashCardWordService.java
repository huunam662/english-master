package com.example.englishmaster_be.Service;

import com.example.englishmaster_be.Model.Request.FlashCard.FlashCardWordRequest;
import com.example.englishmaster_be.entity.FlashCardWordEntity;

import java.util.List;
import java.util.UUID;

public interface IFlashCardWordService {

    void delete(UUID flashCardWordId);

    List<String> searchByFlashCardWord(String keyWord);

    FlashCardWordEntity getFlashCardWordById(UUID flashCardWordId);

    FlashCardWordEntity saveFlashCardWord(FlashCardWordRequest flashCardWordRequest);

}

