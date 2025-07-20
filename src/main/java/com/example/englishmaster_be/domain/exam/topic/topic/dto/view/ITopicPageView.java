package com.example.englishmaster_be.domain.exam.topic.topic.dto.view;


import com.example.englishmaster_be.domain.exam.topic.topic.model.TopicEntity;
import lombok.Data;

public interface ITopicPageView {

    TopicEntity getTopic();
    Long getCountMockTests();
    Long getCountParts();

    @Data
    class TopicPageView implements ITopicPageView {
        private TopicEntity topic;
        private Long countParts;
        private Long countMockTests;
    }
}
