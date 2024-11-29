package com.example.englishmaster_be.Service.impl;

import com.example.englishmaster_be.Common.DTO.Response.FilterResponse;
import com.example.englishmaster_be.Common.Enums.SortByFeedbackFieldsEnum;
import com.example.englishmaster_be.Configuration.global.thread.MessageResponseHolder;
import com.example.englishmaster_be.DTO.Feedback.CreateFeedbackDTO;
import com.example.englishmaster_be.DTO.Feedback.FeedbackFilterRequest;
import com.example.englishmaster_be.DTO.Feedback.UpdateFeedbackDTO;
import com.example.englishmaster_be.Exception.Response.BadRequestException;
import com.example.englishmaster_be.Model.Feedback;
import com.example.englishmaster_be.Model.QFeedback;
import com.example.englishmaster_be.Model.Response.FeedbackResponse;
import com.example.englishmaster_be.Repository.*;
import com.example.englishmaster_be.Service.*;
import com.google.cloud.storage.Blob;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class FeedbackServiceImpl implements IFeedbackService {

    FeedbackRepository feedbackRepository;

    JPAQueryFactory queryFactory;

    IFileStorageService fileStorageService;


    @Override
    public Feedback getFeedbackById(UUID feedbackId) {
        return feedbackRepository.findByFeedbackId(feedbackId)
                .orElseThrow(() -> new BadRequestException("Feedback not found with ID: " + feedbackId));
    }


    private OrderSpecifier<?> buildFeedbackOrderSpecifier(SortByFeedbackFieldsEnum sortBy, Sort.Direction sortDirection) {

        if(sortDirection == null || sortBy == null)
            return null;

        return switch (sortBy) {
            case FeedbackId -> sortDirection.isAscending() ? QFeedback.feedback.id.asc() : QFeedback.feedback.id.desc();
            case Name -> sortDirection.isAscending() ? QFeedback.feedback.name.asc() : QFeedback.feedback.name.desc();
            case Content -> sortDirection.isAscending() ? QFeedback.feedback.content.asc() : QFeedback.feedback.content.desc();
            case Description -> sortDirection.isAscending() ? QFeedback.feedback.description.asc() : QFeedback.feedback.description.desc();
            case CreateAt -> sortDirection.isAscending() ? QFeedback.feedback.createAt.asc() : QFeedback.feedback.createAt.desc();
            case UpdateAt -> sortDirection.isAscending() ? QFeedback.feedback.updateAt.asc() : QFeedback.feedback.updateAt.desc();
        };
    }

    @Override
    public FilterResponse<?> getListFeedbackOfAdmin(FeedbackFilterRequest filterRequest) {

        FilterResponse<FeedbackResponse> filterResponse = FilterResponse.<FeedbackResponse>
                        builder()
                            .pageNumber(filterRequest.getPage())
                            .pageSize(filterRequest.getSize())
                            .offset((long) (filterRequest.getPage() - 1) * filterRequest.getSize())
                            .content(new ArrayList<>())
                        .build();

        JPAQuery<Feedback> query = queryFactory.selectFrom(QFeedback.feedback);

        if (filterRequest.getIsEnable() != null){

            query.where(
                    QFeedback.feedback.enable.eq(filterRequest.getIsEnable())
            );
        }

        if (filterRequest.getSearch() != null && !filterRequest.getSearch().isEmpty()) {

            String likeExpression = "%" + filterRequest.getSearch().toLowerCase().replace(" ", "%") + "%";

            query.where(
                    QFeedback.feedback.content.lower().like(likeExpression)
            );
        }

        long totalElements = Optional.ofNullable(query.select(QFeedback.feedback.count()).fetchOne()).orElse(0L);
        long totalPages = (long) Math.ceil((float) totalElements / filterResponse.getPageSize());
        filterResponse.setTotalElements(totalElements);
        filterResponse.setTotalPages(totalPages);
        filterResponse.withPreviousAndNextPage();

        OrderSpecifier<?> orderSpecifier = buildFeedbackOrderSpecifier(filterRequest.getSortBy(), filterRequest.getDirection());

        if(orderSpecifier != null) query.orderBy(orderSpecifier);

        query.offset(filterResponse.getOffset())
                .limit(filterResponse.getPageSize());

        query.fetch().forEach(Feedback -> {

            filterResponse.getContent().add(new FeedbackResponse(Feedback));
        });

        return filterResponse;

    }

    @Override
    public FilterResponse<?> getListFeedbackOfUser(FeedbackFilterRequest filterRequest) {

        FilterResponse<FeedbackResponse> filterResponse = FilterResponse.<FeedbackResponse>
                builder()
                    .pageNumber(filterRequest.getPage())
                    .pageSize(filterRequest.getSize())
                    .offset((long) (filterRequest.getPage() - 1) * filterRequest.getSize())
                    .content(new ArrayList<>())
                .build();

        OrderSpecifier<?> orderSpecifier = QFeedback.feedback.updateAt.desc();

        JPAQuery<Feedback> query = queryFactory.selectFrom(QFeedback.feedback)
                .where(QFeedback.feedback.enable.eq(Boolean.TRUE))
                .orderBy(orderSpecifier);

        long totalElements = Optional.ofNullable(query.select(QFeedback.feedback.count()).fetchOne()).orElse(0L);
        long totalPages = (long) Math.ceil((float) totalElements / filterResponse.getPageSize());
        filterResponse.setTotalElements(totalElements);
        filterResponse.setTotalPages(totalPages);
        filterResponse.withPreviousAndNextPage();

        query.offset(filterResponse.getOffset())
                .limit(filterResponse.getPageSize());

        query.fetch().forEach(Feedback -> {

            filterResponse.getContent().add(new FeedbackResponse(Feedback));
        });

        return filterResponse;
    }

    @Transactional
    @Override
    public FeedbackResponse saveFeedback(CreateFeedbackDTO feedbackDTO) {

        Feedback feedback;

        if(feedbackDTO instanceof UpdateFeedbackDTO updateFeedbackDTO){
            feedback = getFeedbackById(updateFeedbackDTO.getFeedbackID());
            feedback.setName(updateFeedbackDTO.getName());
            feedback.setDescription(updateFeedbackDTO.getDescription());
            feedback.setContent(updateFeedbackDTO.getContent());
        }
        else feedback = Feedback.builder()
                .name(feedbackDTO.getName())
                .description(feedbackDTO.getDescription())
                .content(feedbackDTO.getContent())
                .build();


        if (feedbackDTO.getAvatar() != null && !feedbackDTO.getAvatar().isEmpty()) {

            String fileName = fileStorageService.nameFile(feedbackDTO.getAvatar());

            Blob blobResultResponse = fileStorageService.save(feedbackDTO.getAvatar(), fileName);

            feedback.setAvatar(blobResultResponse.getName());

        }

        feedback = feedbackRepository.save(feedback);

        return new FeedbackResponse(feedback);
    }

    @Transactional
    @Override
    public void enableFeedback(UUID feedbackId, boolean enable) {
        
        Feedback feedback = getFeedbackById(feedbackId);

        feedback.setEnable(enable);

        feedbackRepository.save(feedback);
        
        if(enable) MessageResponseHolder.setMessage("Enable Feedback successfully");
        
        else MessageResponseHolder.setMessage("Disable Feedback successfully");
    }

    @Transactional
    @Override
    public void deleteFeedback(UUID feedbackId) {

        Feedback feedback = getFeedbackById(feedbackId);

        if(feedback.getAvatar() != null)
            fileStorageService.delete(feedback.getAvatar());

        feedbackRepository.delete(feedback);
    }
}
