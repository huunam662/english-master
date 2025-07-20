package com.example.englishmaster_be.domain.flash_card.word.service;

import com.example.englishmaster_be.advice.exception.ApplicationException;
import com.example.englishmaster_be.common.dto.req.PageOptionsReq;
import com.example.englishmaster_be.domain.flash_card.flash_card.model.FlashCardEntity;
import com.example.englishmaster_be.domain.flash_card.flash_card.service.IFlashCardService;
import com.example.englishmaster_be.domain.flash_card.word.dto.req.FlashCardWordReq;
import com.example.englishmaster_be.domain.flash_card.word.dto.req.UpdateFlashCardWordReq;
import com.example.englishmaster_be.domain.flash_card.word.dto.view.IFlashCardWordPageView;
import com.example.englishmaster_be.domain.flash_card.word.model.FlashCardWordEntity;
import com.example.englishmaster_be.domain.flash_card.word.repository.FlashCardWordJdbcRepository;
import com.example.englishmaster_be.domain.flash_card.word.repository.FlashCardWordRepository;
import com.example.englishmaster_be.domain.flash_card.word.repository.FlashCardWordDslRepository;
import jakarta.persistence.EntityNotFoundException;
import org.apache.coyote.BadRequestException;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class FlashCardWordService implements IFlashCardWordService{

    private final FlashCardWordRepository flashCardWordRepository;
    private final IFlashCardService flashCardService;
    private final FlashCardWordJdbcRepository flashCardWordJdbcRepository;
    private final FlashCardWordDslRepository flashCardWordDslRepository;

    @Lazy
    public FlashCardWordService(FlashCardWordDslRepository flashCardWordDslRepository, FlashCardWordRepository flashCardWordRepository, IFlashCardService flashCardService, FlashCardWordJdbcRepository flashCardWordJdbcRepository) {
        this.flashCardWordRepository = flashCardWordRepository;
        this.flashCardService = flashCardService;
        this.flashCardWordJdbcRepository = flashCardWordJdbcRepository;
        this.flashCardWordDslRepository = flashCardWordDslRepository;
    }

    @Override
    public FlashCardWordEntity getSingleFlashCardWordToId(UUID id) {
        return flashCardWordRepository.findEntityById(id)
                .orElseThrow(() -> new ApplicationException(HttpStatus.NOT_FOUND, "Flash card word not found.", new Exception("id")));
    }

    @Override
    public List<FlashCardWordEntity> getAllFlashCardWords() {
        return flashCardWordRepository.findAllEntity();
    }

    @Override
    public List<FlashCardWordEntity> getAllFlashCardWordsToFlashCardId(UUID flashCardId) {
        FlashCardEntity flashCard = flashCardService.getSingleFlashCardToId(flashCardId);
        return flashCardWordRepository.findAllEntityByFlashCardId(flashCard.getId());
    }

    @Transactional
    @Override
    public List<FlashCardWordEntity> createAllFlashCardWordToFlashCardId(UUID flashCardId, List<FlashCardWordReq> body) {
        FlashCardEntity flashCard = flashCardService.getSingleFlashCardToId(flashCardId);
        List<FlashCardWordEntity> flashCardWords = body.stream().map(
                elm -> {
                    if(elm == null) return null;
                    FlashCardWordEntity flashCardWord = new FlashCardWordEntity();
                    flashCardWord.setFlashCard(flashCard);
                    flashCardWord.setWord(elm.getWord());
                    flashCardWord.setMeaning(elm.getMeaning());
                    flashCardWord.setImage(elm.getImage());
                    flashCardWord.setWordType(elm.getWordType());
                    flashCardWord.setPronunciation(elm.getPronunciation());
                    return flashCardWord;
                }
        ).filter(Objects::nonNull).toList();
        flashCardWordJdbcRepository.insertBatchFlashCardWords(flashCardWords);
        return flashCardWords;
    }

    @Transactional
    @Override
    public FlashCardWordEntity updateSingleFlashCardWordToId(UUID id, FlashCardWordReq body) {
        FlashCardWordEntity flashCardWord = getSingleFlashCardWordToId(id);
        flashCardWord.setWord(body.getWord());
        flashCardWord.setMeaning(body.getMeaning());
        flashCardWord.setImage(body.getImage());
        flashCardWord.setWordType(body.getWordType());
        flashCardWord.setPronunciation(body.getPronunciation());
        return flashCardWordRepository.save(flashCardWord);
    }

    @Transactional
    @Override
    public List<FlashCardWordEntity> updateAllFlashCardWords(List<UpdateFlashCardWordReq> body) {
        List<UUID> bodyIds = body.stream().map(UpdateFlashCardWordReq::getId).toList();
        List<FlashCardWordEntity> flashCardWords = flashCardWordRepository.findAllEntityIdInIds(bodyIds);
        if(bodyIds.size() != flashCardWords.size())
            throw new ApplicationException(HttpStatus.NOT_FOUND, "Flash card word not found.", new Exception("id"));
        Map<UUID, UpdateFlashCardWordReq> bodyGroup = body.stream().collect(
                Collectors.toMap(
                        UpdateFlashCardWordReq::getId,
                        elm -> elm
                )
        );
        for(FlashCardWordEntity flashCardWord : flashCardWords) {
            UpdateFlashCardWordReq req = bodyGroup.get(flashCardWord.getId());
            flashCardWord.setWord(req.getWord());
            flashCardWord.setMeaning(req.getMeaning());
            flashCardWord.setImage(req.getImage());
            flashCardWord.setWordType(req.getWordType());
            flashCardWord.setPronunciation(req.getPronunciation());
        }
        flashCardWordJdbcRepository.updateBatchFlashCardWords(flashCardWords);
        return flashCardWords;
    }

    @Transactional
    @Override
    public FlashCardWordEntity updateImageFlashCardWordToId(UUID id, String imageUrl) {
        if(imageUrl == null || imageUrl.isEmpty())
            throw new ApplicationException(HttpStatus.BAD_REQUEST, "Image url is required.", new Exception("imageUrl"));
        FlashCardWordEntity flashCardWord = getSingleFlashCardWordToId(id);
        flashCardWord.setImage(imageUrl);
        return flashCardWordRepository.save(flashCardWord);
    }

    @Transactional
    @Override
    public void deleteSingleFlashCardWordToId(UUID id) {
        FlashCardWordEntity flashCardWord = getSingleFlashCardWordToId(id);
        flashCardWordRepository.delete(flashCardWord);
    }

    @Transactional
    @Override
    public void deleteAllFlashCardWordsToFlashCardId(UUID flashCardId) {
        List<FlashCardWordEntity> flashCardWords = getAllFlashCardWordsToFlashCardId(flashCardId);
        flashCardWordRepository.deleteAll(flashCardWords);
    }

    @Transactional
    @Override
    public void deleteAllFlashCardWordsToIds(List<UUID> flashCardWordIds) {
        List<FlashCardWordEntity> flashCardWords = flashCardWordRepository.findAllEntityIdInIds(flashCardWordIds);
        if(flashCardWordIds.size() != flashCardWords.size())
            throw new ApplicationException(HttpStatus.NOT_FOUND, "Flash card word not found.", new Exception("id"));
        flashCardWordRepository.deleteAll(flashCardWords);
    }

    @Override
    public Page<IFlashCardWordPageView> getPageFlashCardWord(PageOptionsReq optionsReq) {
        return flashCardWordDslRepository.findPageFlashCardWord(optionsReq);
    }

    @Override
    public Page<IFlashCardWordPageView> getPageFlashCardWordToFlashCardId(UUID flashCardId, PageOptionsReq optionsReq) {
        FlashCardEntity flashCard = flashCardService.getSingleFlashCardToId(flashCardId);
        return flashCardWordDslRepository.findPageFlashCardWordByFlashCard(flashCard, optionsReq);
    }
}
