package com.example.englishmaster_be.DTO.Topic;


import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TopicAndPartDTO {

	UUID topicId;

	UUID partId;

}
