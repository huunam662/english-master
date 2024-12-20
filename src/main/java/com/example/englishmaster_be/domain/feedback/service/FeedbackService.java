package com.example.englishmaster_be.domain.feedback.service;

import com.example.englishmaster_be.common.dto.response.FilterResponse;
import com.example.englishmaster_be.common.thread.MessageResponseHolder;
import com.example.englishmaster_be.domain.file_storage.service.IFileStorageService;
import com.example.englishmaster_be.domain.feedback.dto.request.FeedbackRequest;
import com.example.englishmaster_be.domain.feedback.dto.request.FeedbackFilterRequest;
import com.example.englishmaster_be.domain.upload.dto.request.FileDeleteRequest;
import com.example.englishmaster_be.domain.upload.service.IUploadService;
import com.example.englishmaster_be.domain.user.service.IUserService;
import com.example.englishmaster_be.exception.template.BadRequestException;
import com.example.englishmaster_be.mapper.FeedbackMapper;
import com.example.englishmaster_be.model.feedback.FeedbackRepository;
import com.example.englishmaster_be.model.feedback.QFeedbackEntity;
import com.example.englishmaster_be.helper.FeedbackHelper;
import com.example.englishmaster_be.model.feedback.FeedbackEntity;
import com.example.englishmaster_be.domain.feedback.dto.response.FeedbackResponse;
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
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor(onConstructor_ = {@Autowired, @Lazy})
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FeedbackService implements IFeedbackService {

    JPAQueryFactory queryFactory;

    FeedbackRepository feedbackRepository;

    IUserService userService;

    IUploadService uploadService;



    @Override
    public FeedbackEntity getFeedbackById(UUID feedbackId) {
        return feedbackRepository.findByFeedbackId(feedbackId)
                .orElseThrow(
                        () -> new BadRequestException("Feedback not found")
                );
    }


    @Override
    public FilterResponse<?> getListFeedbackOfAdmin(FeedbackFilterRequest filterRequest) {

        FilterResponse<FeedbackResponse> filterResponse = FilterResponse.<FeedbackResponse>
                        builder()
                            .pageNumber(filterRequest.getPage())
                            .pageSize(filterRequest.getPageSize())
                            .offset((long) (filterRequest.getPage() - 1) * filterRequest.getPageSize())
                        .build();

        BooleanExpression wherePattern = QFeedbackEntity.feedbackEntity.isNotNull();

        if (filterRequest.getIsEnable() != null){

            wherePattern = wherePattern.and(QFeedbackEntity.feedbackEntity.enable.eq(filterRequest.getIsEnable()));
        }

        if (filterRequest.getSearch() != null && !filterRequest.getSearch().isEmpty()) {

            String likeExpression = "%" + filterRequest.getSearch().trim().toLowerCase().replaceAll("\\s+", "%") + "%";

            wherePattern = wherePattern.and(QFeedbackEntity.feedbackEntity.content.likeIgnoreCase(likeExpression));
        }

        long totalElements = Optional.ofNullable(
                                                queryFactory
                                                .select(QFeedbackEntity.feedbackEntity.count())
                                                .where(wherePattern)
                                                .fetchOne()
                                            ).orElse(0L);
        long totalPages = (long) Math.ceil((float) totalElements / filterResponse.getPageSize());
        filterResponse.setTotalPages(totalPages);

        JPAQuery<FeedbackEntity> query = queryFactory
                                    .selectFrom(QFeedbackEntity.feedbackEntity)
                                    .where(wherePattern);

        OrderSpecifier<?> orderSpecifier = FeedbackHelper.buildFeedbackOrderSpecifier(filterRequest.getSortBy(), filterRequest.getDirection());

        query.orderBy(orderSpecifier)
                .offset(filterResponse.getOffset())
                .limit(filterResponse.getPageSize());

        filterResponse.setContent(
                FeedbackMapper.INSTANCE.toFeedbackResponseList(query.fetch())
        );

        return filterResponse;
    }


    @Override
    public FilterResponse<?> getListFeedbackOfUser(FeedbackFilterRequest filterRequest) {

        FilterResponse<FeedbackResponse> filterResponse = FilterResponse.<FeedbackResponse>
                        builder()
                .pageNumber(filterRequest.getPage())
                .pageSize(filterRequest.getPageSize())
                .offset((long) (filterRequest.getPage() - 1) * filterRequest.getPageSize())
                .build();

        BooleanExpression wherePattern = QFeedbackEntity.feedbackEntity.enable.eq(Boolean.TRUE);

        long totalElements = Optional.ofNullable(
                queryFactory
                        .select(QFeedbackEntity.feedbackEntity.count())
                        .from(QFeedbackEntity.feedbackEntity)
                        .where(wherePattern)
                        .fetchOne()
        ).orElse(0L);

        long totalPages = (long) Math.ceil((float) totalElements / filterResponse.getPageSize());
        filterResponse.setTotalPages(totalPages);

        OrderSpecifier<?> orderSpecifier = FeedbackHelper.buildFeedbackOrderSpecifier(filterRequest.getSortBy(), filterRequest.getDirection());

        JPAQuery<FeedbackEntity> query = queryFactory
                .selectFrom(QFeedbackEntity.feedbackEntity)
                .where(wherePattern)
                .orderBy(orderSpecifier)
                .offset(filterResponse.getOffset())
                .limit(filterResponse.getPageSize());

        filterResponse.setContent(
                FeedbackMapper.INSTANCE.toFeedbackResponseList(query.fetch())
        );

        return filterResponse;
    }


    @Transactional
    @Override
    public FeedbackEntity saveFeedback(FeedbackRequest feedbackRequest) {

        UserEntity user = userService.currentUser();

        FeedbackEntity feedback;

        if(feedbackRequest.getFeedbackId() != null)
            feedback = getFeedbackById(feedbackRequest.getFeedbackId());

        else feedback = FeedbackEntity.builder().build();

        FeedbackMapper.INSTANCE.flowToFeedbackEntity(feedbackRequest, feedback);
        feedback.setAvatar(user.getAvatar());
        feedback.setEnable(Boolean.TRUE);

        return feedbackRepository.save(feedback);
    }

    @Transactional
    @Override
    public void enableFeedback(UUID feedbackId, Boolean enable) {
        
        FeedbackEntity feedback = getFeedbackById(feedbackId);

        feedback.setEnable(enable);

        feedbackRepository.save(feedback);
        
        if(enable) MessageResponseHolder.setMessage("Enable FeedbackEntity successfully");
        
        else MessageResponseHolder.setMessage("Disable FeedbackEntity successfully");
    }

    @Transactional
    @Override
    @SneakyThrows
    public void deleteFeedback(UUID feedbackId) {

        FeedbackEntity feedback = getFeedbackById(feedbackId);

        if(feedback.getAvatar() != null)
            uploadService.delete(
                    FileDeleteRequest.builder()
                            .filepath(feedback.getAvatar())
                            .build()
            );

        feedbackRepository.delete(feedback);
    }
}
