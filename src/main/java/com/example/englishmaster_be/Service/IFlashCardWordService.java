package com.example.englishmaster_be.Service;

import com.example.englishmaster_be.DTO.FlashCard.CreateFlashCardWordDTO;
import com.example.englishmaster_be.Model.*;
import com.example.englishmaster_be.Model.Response.FlashCardWordResponse;

import java.util.List;
import java.util.UUID;

public interface IFlashCardWordService {

    void delete(UUID flashCardWordId);

    List<String> searchByFlashCardWord(String keyWord);

    FlashCardWord getWordToID(UUID wordId);

    FlashCardWordResponse saveWordToFlashCard(CreateFlashCardWordDTO createFlashCardWordDTO);

}

