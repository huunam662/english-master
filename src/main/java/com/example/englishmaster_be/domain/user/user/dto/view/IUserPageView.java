package com.example.englishmaster_be.domain.user.user.dto.view;

import com.example.englishmaster_be.domain.user.user.model.UserEntity;
import lombok.Data;

public interface IUserPageView {
    UserEntity getUser();
    Long getCountMockTests();
    Long getCountFlashCards();
    Long getCountComments();
    Long getCountNews();

    @Data
    class PageUserView implements IUserPageView {
        private UserEntity user;
        private Long countMockTests;
        private Long countFlashCards;
        private Long countNews;
        private Long countComments;
    }
}
