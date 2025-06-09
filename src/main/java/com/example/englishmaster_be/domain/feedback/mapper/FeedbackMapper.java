package com.example.englishmaster_be.domain.feedback.mapper;

import com.example.englishmaster_be.domain.feedback.dto.request.FeedbackRequest;
import com.example.englishmaster_be.domain.feedback.model.FeedbackEntity;
import com.example.englishmaster_be.domain.feedback.dto.response.FeedbackResponse;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.util.Collection;
import java.util.List;

@Mapper(builder = @Builder(disableBuilder = true))
public interface FeedbackMapper {

    FeedbackMapper INSTANCE = Mappers.getMapper(FeedbackMapper.class);

    @Mapping(target = "feedbackId", source = "id")
    FeedbackResponse toFeedbackResponse(FeedbackEntity feedback);

    List<FeedbackResponse> toFeedbackResponseList(Collection<FeedbackEntity> feedbackList);

    @Mapping(target = "id", ignore = true)
    FeedbackEntity toFeedbackEntity(FeedbackRequest feedbackRequest);

    @Mapping(target = "id", ignore = true)
    void flowToFeedbackEntity(FeedbackRequest feedbackRequest, @MappingTarget FeedbackEntity feedback);

}
