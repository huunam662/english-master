package com.example.englishmaster_be.DTO.Topic;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class TopicAndPartDTO {
    private UUID topicId;
    private UUID partId;
}
