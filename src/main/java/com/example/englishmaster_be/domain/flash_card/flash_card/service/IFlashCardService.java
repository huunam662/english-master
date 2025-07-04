package com.example.englishmaster_be.domain.flash_card.flash_card.service;

import com.example.englishmaster_be.common.dto.request.PageOptionsReq;
import com.example.englishmaster_be.domain.flash_card.flash_card.dto.request.FlashCardReq;
import com.example.englishmaster_be.domain.flash_card.flash_card.model.FlashCardEntity;
import org.springframework.data.domain.Page;
import java.util.List;
import java.util.UUID;

public interface IFlashCardService {

    FlashCardEntity getSingleFlashCardToId(UUID id);

    List<FlashCardEntity> getAllFlashCards();

    Page<FlashCardEntity> getPageFlashCard(PageOptionsReq pageOptions);

    FlashCardEntity createFlashCard(FlashCardReq flashCardReq);

    FlashCardEntity updateFlashCard(UUID id, FlashCardReq flashCardReq);

    void deleteFlashCard(UUID id);

    void changePublicShared(UUID id, boolean publicShared);
}
