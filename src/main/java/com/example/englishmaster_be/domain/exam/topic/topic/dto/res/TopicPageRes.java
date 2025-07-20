package com.example.englishmaster_be.domain.exam.topic.topic.dto.res;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TopicPageRes {

    private TopicFullRes topic;
    private Long countMockTests;
    private Long countParts;

}
