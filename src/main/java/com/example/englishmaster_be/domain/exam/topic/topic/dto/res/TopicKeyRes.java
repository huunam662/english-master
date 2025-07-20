package com.example.englishmaster_be.domain.exam.topic.topic.dto.res;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Data
@NoArgsConstructor
public class TopicKeyRes {

    private UUID topicId;

    public TopicKeyRes(UUID topicId) {
        this.topicId = topicId;
    }
}
