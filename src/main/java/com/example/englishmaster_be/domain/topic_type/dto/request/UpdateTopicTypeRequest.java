package com.example.englishmaster_be.domain.topic_type.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
public class UpdateTopicTypeRequest extends CreateTopicTypeRequest {

    @NotNull(message = "Topic type id is required.")
    UUID topicTypeId;

}
