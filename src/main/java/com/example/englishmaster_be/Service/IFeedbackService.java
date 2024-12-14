package com.example.englishmaster_be.Service;

import com.example.englishmaster_be.Common.dto.response.FilterResponse;
import com.example.englishmaster_be.Model.Request.Feedback.FeedbackRequest;
import com.example.englishmaster_be.Model.Request.Feedback.FeedbackFilterRequest;
import com.example.englishmaster_be.entity.FeedbackEntity;

import java.util.UUID;

public interface IFeedbackService {

    FeedbackEntity getFeedbackById(UUID feedbackId);

    FilterResponse<?> getListFeedbackOfAdmin(FeedbackFilterRequest filterRequest);

    FilterResponse<?> getListFeedbackOfUser(FeedbackFilterRequest filterRequest);

    FeedbackEntity saveFeedback(FeedbackRequest feedbackRequest);

    void enableFeedback(UUID feedbackId, Boolean enable);

    void deleteFeedback(UUID feedbackId);

}
