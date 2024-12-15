package com.example.englishmaster_be.util;

import com.example.englishmaster_be.common.constant.sort.SortByFeedbackFieldsEnum;

import com.example.englishmaster_be.model.feedback.QFeedbackEntity;
import com.querydsl.core.types.OrderSpecifier;
import org.springframework.data.domain.Sort;

public class FeedBackUtil {

    public static OrderSpecifier<?> buildFeedbackOrderSpecifier(SortByFeedbackFieldsEnum sortBy, Sort.Direction sortDirection) {

        return switch (sortBy) {
            case Name -> sortDirection.isAscending() ? QFeedbackEntity.feedbackEntity.name.asc() : QFeedbackEntity.feedbackEntity.name.desc();
            case Content -> sortDirection.isAscending() ? QFeedbackEntity.feedbackEntity.content.asc() : QFeedbackEntity.feedbackEntity.content.desc();
            case Description -> sortDirection.isAscending() ? QFeedbackEntity.feedbackEntity.description.asc() : QFeedbackEntity.feedbackEntity.description.desc();
            case CreateAt -> sortDirection.isAscending() ? QFeedbackEntity.feedbackEntity.createAt.asc() : QFeedbackEntity.feedbackEntity.createAt.desc();
            case UpdateAt -> sortDirection.isAscending() ? QFeedbackEntity.feedbackEntity.updateAt.asc() : QFeedbackEntity.feedbackEntity.updateAt.desc();
            default -> sortDirection.isAscending() ? QFeedbackEntity.feedbackEntity.id.asc() : QFeedbackEntity.feedbackEntity.id.desc();
        };
    }

}
