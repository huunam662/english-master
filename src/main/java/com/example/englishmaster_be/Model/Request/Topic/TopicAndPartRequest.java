package com.example.englishmaster_be.Model.Request.Topic;


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
