package com.example.englishmaster_be.domain.exam.topic.type.dto.req;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateTopicTypeReq {

    @NotNull(message = "Topic type name is required.")
    @NotEmpty(message = "Topic type name is required.")
    private String topicTypeName;

}
