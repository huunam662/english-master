package com.example.englishmaster_be.domain.topic_type.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class TopicTypeKeyResponse {
    private UUID topicTypeId;
}
