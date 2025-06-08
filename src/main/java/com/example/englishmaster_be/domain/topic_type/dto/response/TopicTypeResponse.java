package com.example.englishmaster_be.domain.topic_type.dto.response;

import lombok.Data;

import java.util.UUID;

@Data
public class TopicTypeResponse {

    UUID topicTypeId;

    String topicTypeName;

}
