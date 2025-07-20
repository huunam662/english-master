package com.example.englishmaster_be.domain.flash_card.word.service;

import com.example.englishmaster_be.common.dto.req.PageOptionsReq;
import com.example.englishmaster_be.domain.flash_card.word.dto.req.FlashCardWordReq;
import com.example.englishmaster_be.domain.flash_card.word.dto.req.UpdateFlashCardWordReq;
import com.example.englishmaster_be.domain.flash_card.word.dto.view.IFlashCardWordPageView;
import com.example.englishmaster_be.domain.flash_card.word.model.FlashCardWordEntity;
import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Page;
import java.util.List;
import java.util.UUID;

public interface IFlashCardWordService {

    FlashCardWordEntity getSingleFlashCardWordToId(UUID id);

    List<FlashCardWordEntity> getAllFlashCardWords();

    List<FlashCardWordEntity> getAllFlashCardWordsToFlashCardId(UUID flashCardId);

    List<FlashCardWordEntity> createAllFlashCardWordToFlashCardId(UUID flashCardId, List<FlashCardWordReq> body);

    FlashCardWordEntity updateSingleFlashCardWordToId(UUID id, FlashCardWordReq body);

    List<FlashCardWordEntity> updateAllFlashCardWords(List<UpdateFlashCardWordReq> body);

    FlashCardWordEntity updateImageFlashCardWordToId(UUID id, String imageUrl);

    void deleteSingleFlashCardWordToId(UUID id);

    void deleteAllFlashCardWordsToFlashCardId(UUID flashCardId);

    void deleteAllFlashCardWordsToIds(List<UUID> flashCardWordIds);

    Page<IFlashCardWordPageView> getPageFlashCardWord(PageOptionsReq optionsReq);

    Page<IFlashCardWordPageView> getPageFlashCardWordToFlashCardId(UUID flashCardId, PageOptionsReq optionsReq);

}
