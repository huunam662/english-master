package com.example.englishmaster_be.domain.exam.topic.type.dto.view;

import com.example.englishmaster_be.domain.exam.topic.type.model.TopicTypeEntity;
import lombok.Data;

public interface ITopicTypePageView {
    TopicTypeEntity getTopicType();
    Long getCountTopics();

    @Data
    class TopicTypePageView implements ITopicTypePageView {
        private TopicTypeEntity topicType;
        private Long countTopics;
    }
}
