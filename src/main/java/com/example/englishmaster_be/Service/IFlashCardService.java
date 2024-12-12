package com.example.englishmaster_be.Service;

import com.example.englishmaster_be.Model.Request.FlashCard.FlashCardRequest;
import com.example.englishmaster_be.entity.FlashCardEntity;
import com.example.englishmaster_be.entity.UserEntity;

import java.util.List;
import java.util.UUID;

public interface IFlashCardService {

    FlashCardEntity getFlashCardById(UUID flashCardId);

    List<FlashCardEntity> getFlashCardByUser(UserEntity user);

    void delete(UUID flashCardId);

    List<FlashCardEntity> getListFlashCardByCurrentUser();

    FlashCardEntity saveFlashCard(FlashCardRequest flashCardRequest);

}
