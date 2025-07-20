package com.example.englishmaster_be.domain.flash_card.flash_card.service;

import com.example.englishmaster_be.advice.exception.ApplicationException;
import com.example.englishmaster_be.common.dto.req.PageOptionsReq;
import com.example.englishmaster_be.domain.flash_card.flash_card.dto.req.FlashCardReq;
import com.example.englishmaster_be.domain.flash_card.flash_card.dto.view.IFlashCardPageView;
import com.example.englishmaster_be.domain.flash_card.flash_card.model.FlashCardEntity;
import com.example.englishmaster_be.domain.flash_card.flash_card.repository.FlashCardRepository;
import com.example.englishmaster_be.domain.flash_card.flash_card.repository.FlashCardDslRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j(topic = "FLASH-CARD-SERVICE")
@Service
public class FlashCardService implements IFlashCardService {

    private final FlashCardRepository flashCardRepository;
    private final FlashCardDslRepository flashCardDslRepository;

    @Lazy
    public FlashCardService(FlashCardRepository flashCardRepository, FlashCardDslRepository flashCardDslRepository) {
        this.flashCardRepository = flashCardRepository;
        this.flashCardDslRepository = flashCardDslRepository;
    }

    @Override
    public FlashCardEntity getSingleFlashCardToId(UUID id) {
        return flashCardRepository.findById(id).orElseThrow(
                () -> new ApplicationException(HttpStatus.NOT_FOUND, "Flashcard not found.", new Exception("id"))
        );
    }

    @Override
    public List<FlashCardEntity> getAllFlashCards() {
        return flashCardRepository.findAll();
    }

    @Override
    public Page<IFlashCardPageView> getPageFlashCard(PageOptionsReq pageOptions) {
        return flashCardDslRepository.findPageFlashCard(pageOptions);
    }

    @Transactional
    @Override
    public FlashCardEntity createFlashCard(FlashCardReq flashCardReq) {
        FlashCardEntity flashCard = new FlashCardEntity();
        flashCard.setTitle(flashCardReq.getTitle());
        flashCard.setDescription(flashCardReq.getDescription());
        flashCard.setImage(flashCardReq.getImage());
        return flashCardRepository.save(flashCard);
    }

    @Transactional
    @Override
    public FlashCardEntity updateFlashCard(UUID id, FlashCardReq flashCardReq) {
        FlashCardEntity flashCard = getSingleFlashCardToId(id);
        flashCard.setTitle(flashCardReq.getTitle());
        flashCard.setDescription(flashCardReq.getDescription());
        flashCard.setImage(flashCardReq.getImage());
        return flashCardRepository.save(flashCard);
    }

    @Transactional
    @Override
    public void deleteFlashCard(UUID id) {
        FlashCardEntity flashCard = getSingleFlashCardToId(id);
        flashCardRepository.delete(flashCard);
    }

    @Transactional
    @Override
    public void changePublicShared(UUID id, boolean publicShared) {
        FlashCardEntity flashCard = getSingleFlashCardToId(id);
        flashCardRepository.updatePublicSharedById(flashCard.getId(), publicShared);
    }
}
