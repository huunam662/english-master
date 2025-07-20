package com.example.englishmaster_be.domain.feedback.dto.view;

import com.example.englishmaster_be.domain.feedback.model.FeedbackEntity;
import lombok.Data;
import lombok.NoArgsConstructor;

public interface IFeedbackPageView {
    FeedbackEntity getFeedback();

    @Data
    @NoArgsConstructor
    class FeedbackPageView implements IFeedbackPageView {
        private FeedbackEntity feedback;
    }
}
