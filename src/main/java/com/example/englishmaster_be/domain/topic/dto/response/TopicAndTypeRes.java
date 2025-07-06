package com.example.englishmaster_be.domain.topic.dto.response;

import com.example.englishmaster_be.domain.topic_type.dto.response.TopicTypeResponse;
import lombok.*;
import lombok.experimental.FieldDefaults;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TopicAndTypeRes {

    UUID topicId;

    String topicName;

    TopicTypeResponse topicType;

}
