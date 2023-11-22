package com.example.englishmaster_be.Service.impl;

import com.example.englishmaster_be.Model.FlashCardWord;
import com.example.englishmaster_be.Repository.*;
import com.example.englishmaster_be.Service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class FlashCardWordServiceImpl implements IFlashCardWordService {

    @Autowired
    private FlashCardWordRepository flashCardWordRepository;

    @Override
    public void save(FlashCardWord flashCardWord) {
        flashCardWordRepository.save(flashCardWord);
    }

    @Override
    public void delete(FlashCardWord flashCardWord) {
        flashCardWordRepository.delete(flashCardWord);
    }

    @Override
    public FlashCardWord findWordToID(UUID wordId) {
        return flashCardWordRepository.findById(wordId).orElseThrow(() -> new IllegalArgumentException("FlashCard word not found with ID: " + wordId));
    }
}
