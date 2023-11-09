package com.example.englishmaster_be.Service.impl;

import com.example.englishmaster_be.Model.FlashCardWord;
import com.example.englishmaster_be.Repository.*;
import com.example.englishmaster_be.Service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FlashCardWordServiceImpl implements IFlashCardWordService {

    @Autowired
    private FlashCardWordRepository flashCardWordRepository;

    @Override
    public void save(FlashCardWord flashCardWord) {
        flashCardWordRepository.save(flashCardWord);
    }
}
