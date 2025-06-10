package com.example.englishmaster_be.domain.flash_card.service;

import com.example.englishmaster_be.advice.exception.template.ErrorHolder;
import com.example.englishmaster_be.common.constant.Role;
import com.example.englishmaster_be.common.constant.error.Error;
import com.example.englishmaster_be.domain.flash_card.dto.projection.IFlashCardField1Projection;
import com.example.englishmaster_be.domain.flash_card.dto.projection.IFlashCardField2Projection;
import com.example.englishmaster_be.domain.flash_card.dto.request.FlashCardFilterRequest;
import com.example.englishmaster_be.domain.flash_card.dto.request.FlashCardRequest;
import com.example.englishmaster_be.domain.flash_card.dto.response.FlashCardKeyResponse;
import com.example.englishmaster_be.domain.flash_card.dto.response.FlashCardResponse;
import com.example.englishmaster_be.domain.flash_card.dto.response.FlashCardUserResponse;
import com.example.englishmaster_be.domain.flash_card.model.QFlashCardEntity;
import com.example.englishmaster_be.domain.flash_card.repository.jdbc.FlashCardJdbcRepository;
import com.example.englishmaster_be.domain.flash_card_word.repository.FlashCardWordRepository;
import com.example.englishmaster_be.domain.upload.dto.request.FileDeleteRequest;
import com.example.englishmaster_be.domain.upload.service.IUploadService;
import com.example.englishmaster_be.domain.flash_card.mapper.FlashCardMapper;
import com.example.englishmaster_be.domain.flash_card.model.FlashCardEntity;
import com.example.englishmaster_be.domain.flash_card.repository.jpa.FlashCardRepository;
import com.example.englishmaster_be.domain.user.model.QUserEntity;
import com.example.englishmaster_be.domain.user.model.UserEntity;
import com.example.englishmaster_be.domain.user.service.IUserService;
import com.example.englishmaster_be.shared.dto.response.FilterResponse;
import com.example.englishmaster_be.shared.helper.QuerydslHelper;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.support.Querydsl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j(topic = "FLASH-CARD-SERVICE")
@Service
@RequiredArgsConstructor(onConstructor_ = {@Lazy})
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FlashCardService implements IFlashCardService {


    FlashCardRepository flashCardRepository;

    FlashCardWordRepository flashCardWordRepository;

    FlashCardJdbcRepository flashCardJdbcRepository;

    IUserService userService;

    IUploadService uploadService;

    JPAQueryFactory queryFactory;

    QuerydslHelper querydslHelper;

    @Override
    public FlashCardEntity getFlashCardById(UUID flashCardId) {
        return flashCardRepository.findByFlashCardId(flashCardId)
                .orElseThrow(
                        () -> new ErrorHolder(Error.RESOURCE_NOT_FOUND, "FlashCardEntity not found with ID: " + flashCardId, false)
                );
    }

    @Override
    public FlashCardEntity getFlashCardToUserById(UUID flashCardId) {
        UserEntity currentUser = userService.currentUser();
        return flashCardRepository.findJoinOwner(flashCardId, currentUser.getUserId())
                .orElseThrow(
                        () -> new ErrorHolder(Error.RESOURCE_NOT_FOUND, "Flash card to user not found.")
                );
    }


    @Override
    public FlashCardResponse getFlashCardResponseById(UUID flashCardId) {

        FlashCardEntity flashCard = flashCardRepository.findByFlashCardId(flashCardId)
                .orElseThrow(
                        () -> new ErrorHolder(Error.RESOURCE_NOT_FOUND, "FlashCardEntity not found with ID: " + flashCardId, false)
                );

        IFlashCardField2Projection countOfFlashCard = flashCardWordRepository.countWordsOfFlashCardId(flashCardId);

        FlashCardResponse flashCardResponse = FlashCardMapper.INSTANCE.toFlashCardResponse(flashCard);
        flashCardResponse.setCountOfWords(countOfFlashCard != null ? countOfFlashCard.getCountOfWords() : 0);
        return flashCardResponse;
    }

    @Override
    public FlashCardUserResponse getFlashCardResponseToUserById(UUID flashCardId) {

        Assert.notNull(flashCardId, "Flash card id is required.");

        UserEntity currentUser = userService.currentUser();

        FlashCardEntity flashCard = flashCardRepository.findJoinOwner(flashCardId, currentUser.getUserId())
                .orElseThrow(
                        () -> new ErrorHolder(Error.RESOURCE_NOT_FOUND, "Flash card to user not found.")
                );

        IFlashCardField2Projection countOfFlashCard = flashCardWordRepository.countWordsOfFlashCardId(flashCardId);

        FlashCardUserResponse flashCardUserResponse = FlashCardMapper.INSTANCE.toFlashCardUserResponse(flashCard);
        flashCardUserResponse.setCountOfWords(countOfFlashCard != null ? countOfFlashCard.getCountOfWords() : 0);

        return flashCardUserResponse;
    }

    @Override
    public FilterResponse<?> getListFlashCard(FlashCardFilterRequest filterRequest) {

        QFlashCardEntity flashCard = QFlashCardEntity.flashCardEntity;
        QUserEntity user = QUserEntity.userEntity;
        BooleanExpression expression = flashCard.flashCardOwner.isNotNull();

        if(filterRequest.getSearch() != null && !filterRequest.getSearch().isEmpty()){
            expression.and(
                    flashCard.flashCardTitle.like("%" + filterRequest.getSearch() + "%")
                            .or(flashCard.flashCardDescription.like("%" + filterRequest.getSearch() + "%"))
            );
        }

        Pageable pageable = PageRequest.of(
                filterRequest.getPage() - 1,
                filterRequest.getPageSize(),
                filterRequest.getDirection(),
                filterRequest.getSortBy().getField()
        );

        JPQLQuery<FlashCardEntity> query = querydslHelper.getInstance(FlashCardEntity.class)
                .applyPagination(
                        pageable,
                        queryFactory.selectFrom(flashCard).where(expression)
                );

        long totalFlashCard = Optional.ofNullable(
                queryFactory.select(flashCard.count())
                        .from(flashCard)
                        .where(expression)
                        .fetchOne()
        ).orElse(0L);

        Page<FlashCardEntity> pageResult = new PageImpl<>(query.fetch(), pageable, totalFlashCard);

        List<FlashCardResponse> flashCards = FlashCardMapper.INSTANCE.toFlashCardResponseList(pageResult.getContent());

        List<UUID> flashCardIds = flashCards.stream().map(FlashCardResponse::getFlashCardId)
                .toList();

        Map<UUID, Integer> countOfWordsGroup = flashCardWordRepository.countWordsInFlashCardIds(flashCardIds)
                .stream().collect(
                        Collectors.toMap(
                                IFlashCardField2Projection::getFlashCardId,
                                IFlashCardField2Projection::getCountOfWords
                        )
                );

        flashCards = flashCards.stream().peek(
                flashCardUser -> flashCardUser.setCountOfWords(countOfWordsGroup.getOrDefault(flashCardUser.getFlashCardId(), 0))
        ).toList();

        return FilterResponse.<FlashCardResponse>builder()
                .pageNumber(pageable.getPageNumber() + 1)
                .pageSize(pageable.getPageSize())
                .offset(pageable.getOffset())
                .totalPages((long) pageResult.getTotalPages())
                .contentLength(pageResult.getContent().size())
                .content(flashCards)
                .build();
    }

    @Override
    public FilterResponse<?> getListFlashCardUser(FlashCardFilterRequest filterRequest) {

        UserEntity userRequest = userService.currentUser();

        QFlashCardEntity flashCard = QFlashCardEntity.flashCardEntity;
        QUserEntity user = QUserEntity.userEntity;
        BooleanExpression expression = flashCard.flashCardOwner.isNotNull()
                .and(flashCard.flashCardOwner.userId.eq(userRequest.getUserId()));

        if(filterRequest.getSearch() != null && !filterRequest.getSearch().isEmpty()){
            expression.and(
                    flashCard.flashCardTitle.like("%" + filterRequest.getSearch() + "%")
                            .or(flashCard.flashCardDescription.like("%" + filterRequest.getSearch() + "%"))
            );
        }

        Pageable pageable = PageRequest.of(
                filterRequest.getPage() - 1,
                filterRequest.getPageSize(),
                filterRequest.getDirection(),
                filterRequest.getSortBy().getField()
                );

        JPQLQuery<FlashCardEntity> query = querydslHelper.getInstance(FlashCardEntity.class)
                .applyPagination(
                        pageable,
                        queryFactory.selectFrom(flashCard)
                        .innerJoin(flashCard.flashCardOwner, user).fetchJoin()
                        .where(expression)
                );

        long totalFlashCard = Optional.ofNullable(
                queryFactory.select(flashCard.count())
                        .from(flashCard)
                        .where(expression)
                        .fetchOne()
        ).orElse(0L);

        Page<FlashCardEntity> pageResult = new PageImpl<>(query.fetch(), pageable, totalFlashCard);

        List<FlashCardUserResponse> flashCardUsers = FlashCardMapper.INSTANCE.toFlashCardUserResponseList(pageResult.getContent());

        List<UUID> flashCardIds = flashCardUsers.stream().map(FlashCardUserResponse::getFlashCardId)
                .toList();

        Map<UUID, Integer> countOfWordsGroup = flashCardWordRepository.countWordsInFlashCardIds(flashCardIds)
                .stream().collect(
                        Collectors.toMap(
                                IFlashCardField2Projection::getFlashCardId,
                                IFlashCardField2Projection::getCountOfWords
                        )
                );

        flashCardUsers = flashCardUsers.stream().peek(
            flashCardUser -> flashCardUser.setCountOfWords(countOfWordsGroup.getOrDefault(flashCardUser.getFlashCardId(), 0))
        ).toList();

        return FilterResponse.<FlashCardUserResponse>builder()
                .pageNumber(pageable.getPageNumber() + 1)
                .pageSize(pageable.getPageSize())
                .offset(pageable.getOffset())
                .totalPages((long) pageResult.getTotalPages())
                .contentLength(pageResult.getContent().size())
                .content(flashCardUsers)
                .build();
    }

    @Transactional
    @Override
    public void deleteFlashCard(UUID flashCardId) {

        Assert.notNull(flashCardId, "Flash card id is required.");

        UserEntity userRequest = userService.currentUser();

        IFlashCardField1Projection projection = flashCardRepository.findInToProjection1dByFlashCardId(flashCardId);

        if(projection == null)
            throw new ErrorHolder(Error.RESOURCE_NOT_FOUND, "Flash card not found.");
        if(userRequest.getRole().getRoleName().equals(Role.USER))
            if(userRequest.getUserId().equals(projection.getOwnerId()))
                throw new ErrorHolder(Error.UNAUTHORIZED, "You are not owner of this flash card.");

        if(projection.getFlashCardImage() != null && !projection.getFlashCardImage().isEmpty()) {
            try {
                uploadService.delete(
                        FileDeleteRequest.builder()
                                .filepath(projection.getFlashCardImage())
                                .build()
                );
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }

        flashCardWordRepository.deleteAllByFlashCardId(flashCardId);
        flashCardRepository.deleteByFlashCardId(flashCardId);
    }

    @Transactional
    @Override
    public FlashCardKeyResponse createFlashCard(FlashCardRequest flashCardRequest) {

        Assert.notNull(flashCardRequest, "Flash card to create is required.");
        if(flashCardRequest.getFlashCardTitle() == null || flashCardRequest.getFlashCardTitle().isEmpty())
            throw new ErrorHolder(Error.BAD_REQUEST, "Flash card title is required.");

        UserEntity userRequest = userService.currentUser();

        FlashCardEntity flashCard = FlashCardMapper.INSTANCE.toFlashCard(flashCardRequest);
        flashCard.setFlashCardId(UUID.randomUUID());
        flashCard.setUserCreate(userRequest);
        flashCard.setUserUpdate(userRequest);

        if(userRequest.getRole().getRoleName().equals(Role.USER))
            flashCard.setFlashCardOwner(userRequest);

        flashCardJdbcRepository.insertFlashCard(flashCard);

        return FlashCardKeyResponse.builder()
                .flashCardId(flashCard.getFlashCardId())
                .build();
    }

    @Transactional
    @Override
    public FlashCardKeyResponse updateFlashCard(UUID flashCardId, FlashCardRequest flashCardRequest) {

        Assert.notNull(flashCardId, "Flash card id is required.");
        Assert.notNull(flashCardRequest, "Flash card to update is required.");
        if(flashCardRequest.getFlashCardTitle() == null || flashCardRequest.getFlashCardTitle().isEmpty())
            throw new ErrorHolder(Error.BAD_REQUEST, "Flash card title is required.");

        UserEntity userRequest = userService.currentUser();

        IFlashCardField1Projection prjection1 = flashCardRepository.findInToProjection1dByFlashCardId(flashCardId);

        if(prjection1 == null)
            throw new ErrorHolder(Error.BAD_REQUEST, "Flash card not found.");
        if(userRequest.getRole().getRoleName().equals(Role.USER))
            if(userRequest.getUserId().equals(prjection1.getOwnerId()))
                throw new ErrorHolder(Error.UNAUTHORIZED, "You are not owner of this flash card.");

        String currentImage = prjection1.getFlashCardImage();
        if(currentImage != null && !currentImage.isEmpty() && currentImage.equals(flashCardRequest.getFlashCardImage())){
            try {
                uploadService.delete(
                        FileDeleteRequest.builder()
                                .filepath(prjection1.getFlashCardImage())
                                .build()
                );
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }

        flashCardJdbcRepository.updateFlashCard(prjection1.getFlashCardId(), flashCardRequest, userRequest);

        return FlashCardKeyResponse.builder()
                .flashCardId(prjection1.getFlashCardId())
                .build();
    }
}
