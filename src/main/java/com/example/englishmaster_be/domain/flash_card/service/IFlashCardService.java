package com.example.englishmaster_be.domain.flash_card.service;

import com.example.englishmaster_be.domain.flash_card.dto.request.FlashCardFilterRequest;
import com.example.englishmaster_be.domain.flash_card.dto.request.FlashCardRequest;
import com.example.englishmaster_be.domain.flash_card.dto.response.FlashCardKeyResponse;
import com.example.englishmaster_be.domain.flash_card.dto.response.FlashCardResponse;
import com.example.englishmaster_be.domain.flash_card.dto.response.FlashCardUserResponse;
import com.example.englishmaster_be.domain.flash_card.model.FlashCardEntity;
import com.example.englishmaster_be.domain.user.model.UserEntity;
import com.example.englishmaster_be.shared.dto.response.FilterResponse;

import java.util.List;
import java.util.UUID;

public interface IFlashCardService {

    FlashCardEntity getFlashCardById(UUID flashCardId);

    FlashCardEntity getFlashCardToUserById(UUID flashCardId);

    FlashCardResponse getFlashCardResponseById(UUID flashCardId);

    FlashCardUserResponse getFlashCardResponseToUserById(UUID flashCardId);

    void deleteFlashCard(UUID flashCardId);

    FilterResponse<?> getListFlashCardUser(FlashCardFilterRequest filterRequest);

    FilterResponse<?> getListFlashCard(FlashCardFilterRequest filterRequest);

    FlashCardKeyResponse createFlashCard(FlashCardRequest flashCardRequest);

    FlashCardKeyResponse updateFlashCard(UUID flashCardId, FlashCardRequest flashCardRequest);

}
