package com.example.englishmaster_be.Service;

import com.example.englishmaster_be.Model.*;

import java.util.List;
import java.util.UUID;

public interface IFlashCardService {
    FlashCard findFlashCardToId(UUID flashCardId);

    List<FlashCard> findFlashCardToUser(User user);

    void saveFlashCard(FlashCard flashCard);

    void delete(FlashCard flashCard);
}
