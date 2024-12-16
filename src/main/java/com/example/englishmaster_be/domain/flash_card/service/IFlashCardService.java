package com.example.englishmaster_be.domain.flash_card.service;

import com.example.englishmaster_be.domain.flash_card.dto.request.FlashCardRequest;
import com.example.englishmaster_be.model.flash_card.FlashCardEntity;
import com.example.englishmaster_be.model.user.UserEntity;

import java.util.List;
import java.util.UUID;

public interface IFlashCardService {

    FlashCardEntity getFlashCardById(UUID flashCardId);

    List<FlashCardEntity> getFlashCardByUser(UserEntity user);

    void delete(UUID flashCardId);

    List<FlashCardEntity> getListFlashCardByCurrentUser();

    FlashCardEntity saveFlashCard(FlashCardRequest flashCardRequest);

}
