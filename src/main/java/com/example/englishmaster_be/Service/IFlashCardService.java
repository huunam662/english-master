package com.example.englishmaster_be.Service;

import com.example.englishmaster_be.DTO.FlashCard.SaveFlashCardDTO;
import com.example.englishmaster_be.Model.*;
import com.example.englishmaster_be.Model.Response.FlashCardListWordsResponse;
import com.example.englishmaster_be.Model.Response.FlashCardResponse;

import java.util.List;
import java.util.UUID;

public interface IFlashCardService {

    FlashCard getFlashCardToId(UUID flashCardId);

    List<FlashCard> getFlashCardToUser(User user);

    void delete(UUID flashCardId);

    FlashCardListWordsResponse getWordToFlashCard(UUID flashCardId);

    List<FlashCardResponse> getListFlashCardUser();

    FlashCardResponse saveFlashCard(SaveFlashCardDTO createFlashCardDTO);

}
