package com.example.englishmaster_be.domain.exam.topic.type.dto.req;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
public class UpdateTopicTypeReq extends CreateTopicTypeReq {

    @NotNull(message = "Topic type id is required.")
    private UUID topicTypeId;

}
