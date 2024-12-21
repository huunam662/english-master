package com.example.englishmaster_be.domain.flash_card_word.service;

import com.example.englishmaster_be.domain.file_storage.dto.response.FileResponse;
import com.example.englishmaster_be.domain.flash_card.service.IFlashCardService;
import com.example.englishmaster_be.domain.upload.dto.request.FileDeleteRequest;
import com.example.englishmaster_be.domain.upload.service.IUploadService;
import com.example.englishmaster_be.domain.user.service.IUserService;
import com.example.englishmaster_be.domain.flash_card_word.dto.request.FlashCardWordRequest;
import com.example.englishmaster_be.mapper.FlashCardWordMapper;
import com.example.englishmaster_be.model.flash_card.FlashCardEntity;
import com.example.englishmaster_be.model.flash_card_word.FlashCardWordEntity;
import com.example.englishmaster_be.model.flash_card_word.FlashCardWordRepository;
import com.example.englishmaster_be.model.flash_card_word.QFlashCardWordEntity;
import com.example.englishmaster_be.model.user.UserEntity;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;



@Service
@RequiredArgsConstructor(onConstructor_ = {@Autowired, @Lazy})
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FlashCardWordService implements IFlashCardWordService {

    JPAQueryFactory jpaQueryFactory;

    FlashCardWordRepository flashCardWordRepository;

    IUserService userService;

    IFlashCardService flashCardService;

    IUploadService uploadService;



    @Override
    public FlashCardWordEntity getFlashCardWordById(UUID flashCardWordId) {

        return flashCardWordRepository.findById(flashCardWordId)
                .orElseThrow(
                        () -> new IllegalArgumentException("FlashCardEntity word not found with ID: " + flashCardWordId)
                );
    }

    @Override
    public List<String> searchByFlashCardWord(String keyWord) {

        OrderSpecifier<?> orderSpecifier = QFlashCardWordEntity.flashCardWordEntity.word.asc();

        String likePattern = "%" + keyWord.trim().toLowerCase().replaceAll("\\s+", "%") + "%";

        BooleanExpression queryConditionPattern = QFlashCardWordEntity.flashCardWordEntity.word.likeIgnoreCase(likePattern);

        JPAQuery<FlashCardWordEntity> query = jpaQueryFactory.selectFrom(QFlashCardWordEntity.flashCardWordEntity);

        int offsetKey = 0;
        int limitKey = 0;

        query.where(queryConditionPattern)
                .orderBy(orderSpecifier)
                .offset(offsetKey)
                .limit(limitKey);

        return query.fetch().stream().map(FlashCardWordEntity::getWord).toList();
    }

    @Transactional
    @Override
    public void delete(UUID flashCardWordId) {

        FlashCardWordEntity flashCardWord = getFlashCardWordById(flashCardWordId);

        flashCardWordRepository.delete(flashCardWord);
    }

    @Transactional
    @Override
    @SneakyThrows
    public FlashCardWordEntity saveFlashCardWord(FlashCardWordRequest flashCardWordRequest) {

        UserEntity user = userService.currentUser();

        FlashCardWordEntity flashCardWord;

        if(flashCardWordRequest.getFlashCardWordId() != null)
            flashCardWord = getFlashCardWordById(flashCardWordRequest.getFlashCardWordId());

        else {

            FlashCardEntity flashCard = flashCardService.getFlashCardById(flashCardWordRequest.getFlashCardId());

            flashCardWord = FlashCardWordEntity.builder()
                    .flashCard(flashCard)
                    .createAt(LocalDateTime.now())
                    .userCreate(user)
                    .build();
        }

        FlashCardWordMapper.INSTANCE.flowToFlashCardWordEntity(flashCardWordRequest, flashCardWord);
        flashCardWord.setUserUpdate(user);
        flashCardWord.setUpdateAt(LocalDateTime.now());

        if(flashCardWordRequest.getImage() != null && !flashCardWordRequest.getImage().isEmpty()){

            if(flashCardWord.getImage() != null && !flashCardWord.getImage().isEmpty())
                uploadService.delete(
                        FileDeleteRequest.builder()
                                .filepath(flashCardWord.getImage())
                                .build()
                );

            flashCardWord.setImage(flashCardWordRequest.getImage());
        }

        return flashCardWordRepository.save(flashCardWord);
    }
}
