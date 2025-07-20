package com.example.englishmaster_be.domain.exam.topic.type.dto.res;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
public class TopicTypeRes {

    private UUID topicTypeId;

    private String topicTypeName;

    private LocalDateTime createAt;

    private LocalDateTime updateAt;
}
