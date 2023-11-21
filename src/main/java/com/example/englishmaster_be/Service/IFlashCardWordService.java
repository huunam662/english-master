package com.example.englishmaster_be.Service;

import com.example.englishmaster_be.Model.*;

import java.util.UUID;

public interface IFlashCardWordService {
    void save(FlashCardWord flashCardWord);

    void delete(FlashCardWord flashCardWord);

    FlashCardWord findWordToID(UUID wordId);
}
