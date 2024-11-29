package com.example.englishmaster_be.Service;

import com.example.englishmaster_be.Common.DTO.Response.FilterResponse;
import com.example.englishmaster_be.DTO.Feedback.CreateFeedbackDTO;
import com.example.englishmaster_be.DTO.Feedback.FeedbackFilterRequest;
import com.example.englishmaster_be.Model.*;
import com.example.englishmaster_be.Model.Response.FeedbackResponse;

import java.util.UUID;

public interface IFeedbackService {

    Feedback getFeedbackById(UUID feedbackId);

    FilterResponse<?> getListFeedbackOfAdmin(FeedbackFilterRequest filterRequest);

    FilterResponse<?> getListFeedbackOfUser(FeedbackFilterRequest filterRequest);

    FeedbackResponse saveFeedback(CreateFeedbackDTO createFeedbackDTO);

    void enableFeedback(UUID feedbackId, boolean enable);

    void deleteFeedback(UUID feedbackId);

}
