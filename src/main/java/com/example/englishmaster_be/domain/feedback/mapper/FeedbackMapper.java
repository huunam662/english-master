package com.example.englishmaster_be.domain.feedback.mapper;

import com.example.englishmaster_be.domain.feedback.dto.req.FeedbackReq;
import com.example.englishmaster_be.domain.feedback.dto.res.FeedbackPageRes;
import com.example.englishmaster_be.domain.feedback.dto.view.IFeedbackPageView;
import com.example.englishmaster_be.domain.feedback.model.FeedbackEntity;
import com.example.englishmaster_be.domain.feedback.dto.res.FeedbackRes;
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
    FeedbackRes toFeedbackResponse(FeedbackEntity feedback);

    List<FeedbackRes> toFeedbackResponseList(Collection<FeedbackEntity> feedbackList);

    @Mapping(target = "id", ignore = true)
    FeedbackEntity toFeedbackEntity(FeedbackReq feedbackRequest);

    @Mapping(target = "id", ignore = true)
    void flowToFeedbackEntity(FeedbackReq feedbackRequest, @MappingTarget FeedbackEntity feedback);

    FeedbackPageRes toFeedbackPageRes(IFeedbackPageView feedbackPageView);

    List<FeedbackPageRes> toFeedbackPageResList(Collection<IFeedbackPageView> feedbackPageViewList);
}
