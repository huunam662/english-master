package com.example.englishmaster_be.domain.feedback.util;

import com.example.englishmaster_be.common.constant.sort.FeedbackSortBy;
import com.example.englishmaster_be.domain.feedback.model.QFeedbackEntity;
import com.querydsl.core.types.OrderSpecifier;
import org.springframework.data.domain.Sort;

public class FeedbackUtil {

    public static OrderSpecifier<?> buildFeedbackOrderSpecifier(FeedbackSortBy sortBy, Sort.Direction sortDirection) {

        boolean isAscending = sortDirection != null && sortDirection.isAscending();

        return switch (sortBy) {
            case Name -> isAscending ? QFeedbackEntity.feedbackEntity.name.asc() : QFeedbackEntity.feedbackEntity.name.desc();
            case Content -> isAscending ? QFeedbackEntity.feedbackEntity.content.asc() : QFeedbackEntity.feedbackEntity.content.desc();
            case Description -> isAscending ? QFeedbackEntity.feedbackEntity.description.asc() : QFeedbackEntity.feedbackEntity.description.desc();
            case CreateAt -> isAscending ? QFeedbackEntity.feedbackEntity.createAt.asc() : QFeedbackEntity.feedbackEntity.createAt.desc();
            case UpdateAt -> isAscending ? QFeedbackEntity.feedbackEntity.updateAt.asc() : QFeedbackEntity.feedbackEntity.updateAt.desc();
            default -> isAscending ? QFeedbackEntity.feedbackEntity.id.asc() : QFeedbackEntity.feedbackEntity.id.desc();
        };
    }

}
