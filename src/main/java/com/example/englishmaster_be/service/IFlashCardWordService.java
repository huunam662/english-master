package com.example.englishmaster_be.service;

import com.example.englishmaster_be.model.*;

import java.util.List;
import java.util.UUID;

public interface IFlashCardWordService {
    void save(FlashCardWord flashCardWord);

    void delete(FlashCardWord flashCardWord);

    List<FlashCardWord> searchByFlashCardWord(String query);
    FlashCardWord findWordToID(UUID wordId);
}

