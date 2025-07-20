package com.example.englishmaster_be.domain.exam.topic.topic.dto.res;

import com.example.englishmaster_be.domain.exam.topic.type.dto.res.TopicTypeRes;
import lombok.*;

import java.util.UUID;

@Data
@NoArgsConstructor
public class TopicAndTypeRes {

    private UUID topicId;
    private String topicName;
    private TopicTypeRes topicType;

}
