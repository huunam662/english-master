package com.example.englishmaster_be.model.request.Topic;


import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TopicAndPartRequest {

	UUID topicId;

	UUID partId;

}
