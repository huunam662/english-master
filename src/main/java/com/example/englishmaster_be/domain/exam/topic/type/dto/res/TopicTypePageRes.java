package com.example.englishmaster_be.domain.exam.topic.type.dto.res;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TopicTypePageRes {
    private TopicTypeFullRes topicType;
    private Long countTopics;
}
