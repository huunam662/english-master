package com.example.englishmaster_be.domain.part.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PartTopicResponse {

    UUID partId;

    String partName;

    String partType;

    String partDescription;

    UUID topicId;
}
