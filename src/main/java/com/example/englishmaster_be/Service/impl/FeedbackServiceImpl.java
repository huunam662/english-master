package com.example.englishmaster_be.Service.impl;

import com.example.englishmaster_be.Common.dto.response.FilterResponse;
import com.example.englishmaster_be.Common.enums.sort.SortByFeedbackFieldsEnum;
import com.example.englishmaster_be.Configuration.global.thread.MessageResponseHolder;
import com.example.englishmaster_be.Model.Request.Feedback.FeedbackRequest;
import com.example.englishmaster_be.Model.Request.Feedback.FeedbackFilterRequest;
import com.example.englishmaster_be.Exception.template.BadRequestException;
import com.example.englishmaster_be.Mapper.FeedbackMapper;
import com.example.englishmaster_be.Util.FeedBackUtil;
import com.example.englishmaster_be.entity.FeedbackEntity;
import com.example.englishmaster_be.entity.QFeedbackEntity;
import com.example.englishmaster_be.Model.Response.FeedbackResponse;
import com.example.englishmaster_be.Repository.*;
import com.example.englishmaster_be.Service.*;
import com.google.cloud.storage.Blob;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor(onConstructor_ = {@Autowired, @Lazy})
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FeedbackServiceImpl implements IFeedbackService {

    JPAQueryFactory queryFactory;

    FeedbackRepository feedbackRepository;

    IFileStorageService fileStorageService;


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

        OrderSpecifier<?> orderSpecifier = FeedBackUtil.buildFeedbackOrderSpecifier(filterRequest.getSortBy(), filterRequest.getDirection());

        if(orderSpecifier != null) query.orderBy(orderSpecifier);

        query.offset(filterResponse.getOffset())
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

        OrderSpecifier<?> orderSpecifier = FeedBackUtil.buildFeedbackOrderSpecifier(filterRequest.getSortBy(), filterRequest.getDirection());

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

        FeedbackEntity feedback;

        if(feedbackRequest.getFeedbackId() != null)
            feedback = getFeedbackById(feedbackRequest.getFeedbackId());

        else feedback = FeedbackEntity.builder()
                .createAt(LocalDateTime.now())
                .build();

        FeedbackMapper.INSTANCE.flowToFeedbackEntity(feedbackRequest, feedback);
        feedback.setEnable(Boolean.TRUE);

        if (feedbackRequest.getAvatar() != null && !feedbackRequest.getAvatar().isEmpty()) {

            Blob blobResultResponse = fileStorageService.save(feedbackRequest.getAvatar());

            feedback.setAvatar(blobResultResponse.getName());
        }

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
    public void deleteFeedback(UUID feedbackId) {

        FeedbackEntity feedback = getFeedbackById(feedbackId);

        if(feedback.getAvatar() != null)
            fileStorageService.delete(feedback.getAvatar());

        feedbackRepository.delete(feedback);
    }
}
