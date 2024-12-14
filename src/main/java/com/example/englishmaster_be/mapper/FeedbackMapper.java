package com.example.englishmaster_be.mapper;

import com.example.englishmaster_be.model.request.Feedback.FeedbackRequest;
import com.example.englishmaster_be.entity.FeedbackEntity;
import com.example.englishmaster_be.model.response.FeedbackResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface FeedbackMapper {

    FeedbackMapper INSTANCE = Mappers.getMapper(FeedbackMapper.class);

    FeedbackResponse toFeedbackResponse(FeedbackEntity feedback);

    List<FeedbackResponse> toFeedbackResponseList(List<FeedbackEntity> feedbackList);

    @Mapping(target = "avatar", ignore = true)
    FeedbackEntity toFeedbackEntity(FeedbackRequest feedbackRequest);

    @Mapping(target = "avatar", ignore = true)
    void flowToFeedbackEntity(FeedbackRequest feedbackRequest, @MappingTarget FeedbackEntity feedback);

}
