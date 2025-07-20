package com.example.englishmaster_be.domain.feedback.service;

import com.example.englishmaster_be.common.dto.req.PageOptionsReq;
import com.example.englishmaster_be.domain.feedback.dto.req.FeedbackReq;
import com.example.englishmaster_be.domain.feedback.dto.view.IFeedbackPageView;
import com.example.englishmaster_be.domain.feedback.model.FeedbackEntity;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface IFeedbackService {

    FeedbackEntity getFeedbackById(UUID feedbackId);

    Page<IFeedbackPageView> getPageFeedback(PageOptionsReq optionsReq);


    FeedbackEntity saveFeedback(FeedbackReq feedbackRequest);

    void enableFeedback(UUID feedbackId, Boolean enable);

    void deleteFeedback(UUID feedbackId);

}
