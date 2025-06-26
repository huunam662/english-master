package com.example.englishmaster_be.domain.topic_type.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateTopicTypeRequest {

    @NotNull(message = "Topic type name is required.")
    @NotEmpty(message = "Topic type name is required.")
    String topicTypeName;

}
