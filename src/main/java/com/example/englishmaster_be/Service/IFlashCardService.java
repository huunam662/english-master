package com.example.englishmaster_be.Service;

import com.example.englishmaster_be.DTO.FlashCard.CreateFlashCardDTO;
import com.example.englishmaster_be.DTO.FlashCard.CreateFlashCardWordDTO;
import com.example.englishmaster_be.Model.*;
import com.example.englishmaster_be.Model.Response.FlashCardListWordsResponse;
import com.example.englishmaster_be.Model.Response.FlashCardResponse;
import com.example.englishmaster_be.Model.Response.FlashCardWordResponse;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.UUID;

public interface IFlashCardService {

    FlashCard getFlashCardToId(UUID flashCardId);

    List<FlashCard> getFlashCardToUser(User user);

    void delete(UUID flashCardId);

    FlashCardListWordsResponse getWordToFlashCard(UUID flashCardId);

    List<FlashCardResponse> getListFlashCardUser();

    FlashCardResponse saveFlashCard(CreateFlashCardDTO createFlashCardDTO);

}
