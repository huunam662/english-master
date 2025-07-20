package com.example.englishmaster_be.domain.exam.topic.topic.dto.res;


import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class TopicPageToPackRes extends TopicFullRes{

    private Long countMockTests;
    private Long countParts;

}
