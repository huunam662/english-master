package com.example.englishmaster_be.domain.feedback.service;

import com.example.englishmaster_be.common.dto.response.FilterResponse;
import com.example.englishmaster_be.domain.feedback.dto.request.FeedbackRequest;
import com.example.englishmaster_be.domain.feedback.dto.request.FeedbackFilterRequest;
import com.example.englishmaster_be.model.feedback.FeedbackEntity;

import java.util.UUID;

public interface IFeedbackService {

    FeedbackEntity getFeedbackById(UUID feedbackId);

    FilterResponse<?> getListFeedbackOfAdmin(FeedbackFilterRequest filterRequest);

    FilterResponse<?> getListFeedbackOfUser(FeedbackFilterRequest filterRequest);

    FeedbackEntity saveFeedback(FeedbackRequest feedbackRequest);

    void enableFeedback(UUID feedbackId, Boolean enable);

    void deleteFeedback(UUID feedbackId);

}
