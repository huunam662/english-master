package com.example.englishmaster_be.DTO.Content;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateContentDTO {

    UUID contentId;

    UUID topicId;

    String contentType;

    String contentData;

    String code;
}
