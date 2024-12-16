package com.example.englishmaster_be.mapper;

import com.example.englishmaster_be.domain.feedback.dto.request.FeedbackRequest;
import com.example.englishmaster_be.domain.feedback.dto.response.FeedbackResponse;
import com.example.englishmaster_be.model.feedback.FeedbackEntity;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-12-16T12:56:24+0700",
    comments = "version: 1.6.2, compiler: javac, environment: Java 17.0.12 (Oracle Corporation)"
)
public class FeedbackMapperImpl implements FeedbackMapper {

    @Override
    public FeedbackResponse toFeedbackResponse(FeedbackEntity feedback) {
        if ( feedback == null ) {
            return null;
        }

        FeedbackResponse.FeedbackResponseBuilder feedbackResponse = FeedbackResponse.builder();

        feedbackResponse.name( feedback.getName() );
        feedbackResponse.description( feedback.getDescription() );
        feedbackResponse.avatar( feedback.getAvatar() );
        feedbackResponse.content( feedback.getContent() );
        feedbackResponse.createAt( feedback.getCreateAt() );
        feedbackResponse.updateAt( feedback.getUpdateAt() );
        feedbackResponse.enable( feedback.getEnable() );

        return feedbackResponse.build();
    }

    @Override
    public List<FeedbackResponse> toFeedbackResponseList(List<FeedbackEntity> feedbackList) {
        if ( feedbackList == null ) {
            return null;
        }

        List<FeedbackResponse> list = new ArrayList<FeedbackResponse>( feedbackList.size() );
        for ( FeedbackEntity feedbackEntity : feedbackList ) {
            list.add( toFeedbackResponse( feedbackEntity ) );
        }

        return list;
    }

    @Override
    public FeedbackEntity toFeedbackEntity(FeedbackRequest feedbackRequest) {
        if ( feedbackRequest == null ) {
            return null;
        }

        FeedbackEntity.FeedbackEntityBuilder feedbackEntity = FeedbackEntity.builder();

        feedbackEntity.name( feedbackRequest.getName() );
        feedbackEntity.content( feedbackRequest.getContent() );
        feedbackEntity.description( feedbackRequest.getDescription() );

        return feedbackEntity.build();
    }

    @Override
    public void flowToFeedbackEntity(FeedbackRequest feedbackRequest, FeedbackEntity feedback) {
        if ( feedbackRequest == null ) {
            return;
        }

        feedback.setName( feedbackRequest.getName() );
        feedback.setContent( feedbackRequest.getContent() );
        feedback.setDescription( feedbackRequest.getDescription() );
    }
}
