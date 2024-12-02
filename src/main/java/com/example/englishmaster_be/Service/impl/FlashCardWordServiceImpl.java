package com.example.englishmaster_be.Service.impl;

import com.example.englishmaster_be.DTO.FlashCard.CreateFlashCardWordDTO;
import com.example.englishmaster_be.DTO.FlashCard.UpdateFlashCardWordDTO;
import com.example.englishmaster_be.Exception.Response.BadRequestException;
import com.example.englishmaster_be.Model.FlashCard;
import com.example.englishmaster_be.Model.FlashCardWord;
import com.example.englishmaster_be.Model.QFlashCardWord;
import com.example.englishmaster_be.Model.Response.ExceptionResponseModel;
import com.example.englishmaster_be.Model.Response.FlashCardWordResponse;
import com.example.englishmaster_be.Model.Response.ResponseModel;
import com.example.englishmaster_be.Model.User;
import com.example.englishmaster_be.Repository.*;
import com.example.englishmaster_be.Service.*;
import com.google.cloud.storage.Blob;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPQLQueryFactory;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.json.simple.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FlashCardWordServiceImpl implements IFlashCardWordService {

    FlashCardWordRepository flashCardWordRepository;

    IUserService userService;

    IFlashCardService flashCardService;

    IFileStorageService fileStorageService;

    JPAQueryFactory jpaQueryFactory;


    @Override
    public FlashCardWord getWordToID(UUID wordId) {

        return flashCardWordRepository.findById(wordId)
                .orElseThrow(
                        () -> new IllegalArgumentException("FlashCard word not found with ID: " + wordId)
                );
    }

    @Override
    public List<String> searchByFlashCardWord(String keyWord) {

        OrderSpecifier<?> orderSpecifier = QFlashCardWord.flashCardWord.word.asc();

        String likePattern = "%" + keyWord.trim().toLowerCase().replaceAll("\\s+", "%") + "%";

        BooleanExpression queryConditionPattern = QFlashCardWord.flashCardWord.word.likeIgnoreCase(likePattern);

        JPAQuery<FlashCardWord> query = jpaQueryFactory.selectFrom(QFlashCardWord.flashCardWord);

        int offsetKey = 0;
        int limitKey = 0;

        query.where(queryConditionPattern)
                .orderBy(orderSpecifier)
                .offset(offsetKey)
                .limit(limitKey);

        return query.fetch().stream().map(FlashCardWord::getWord).toList();
    }

    @Transactional
    @Override
    public void delete(UUID flashCardWordId) {

        FlashCardWord flashCardWord = getWordToID(flashCardWordId);

        flashCardWordRepository.delete(flashCardWord);
    }

    @Transactional
    @Override
    public FlashCardWordResponse saveWordToFlashCard(CreateFlashCardWordDTO createFlashCardWordDTO) {

        User user = userService.currentUser();

        FlashCardWord flashCardWord;

        if(createFlashCardWordDTO instanceof UpdateFlashCardWordDTO updateFlashCardWordDTO){

            flashCardWord = getWordToID(updateFlashCardWordDTO.getFlashCardWordId());

            flashCardWord.setWord(updateFlashCardWordDTO.getWord());
            flashCardWord.setDefine(updateFlashCardWordDTO.getDefine());
            flashCardWord.setType(updateFlashCardWordDTO.getType());
            flashCardWord.setSpelling(updateFlashCardWordDTO.getSpelling());
            flashCardWord.setExample(updateFlashCardWordDTO.getExample());
            flashCardWord.setExample(updateFlashCardWordDTO.getExample());
            flashCardWord.setNote(updateFlashCardWordDTO.getNote());
        }
        else {

            FlashCard flashCard = flashCardService.getFlashCardToId(createFlashCardWordDTO.getFlashCardId());

            flashCardWord = FlashCardWord.builder()
                    .word(createFlashCardWordDTO.getWord())
                    .define(createFlashCardWordDTO.getDefine())
                    .type(createFlashCardWordDTO.getType())
                    .spelling(createFlashCardWordDTO.getSpelling())
                    .example(createFlashCardWordDTO.getExample())
                    .note(createFlashCardWordDTO.getNote())
                    .flashCard(flashCard)
                    .build();
        }

        flashCardWord.setUserCreate(user);
        flashCardWord.setUserUpdate(user);
        flashCardWord.setUpdateAt(LocalDateTime.now());

        if(createFlashCardWordDTO.getImage() != null){

            Blob blobResponse = fileStorageService.save(createFlashCardWordDTO.getImage());
            flashCardWord.setImage(blobResponse.getName());
        }

        return new FlashCardWordResponse(flashCardWord);
    }
}
