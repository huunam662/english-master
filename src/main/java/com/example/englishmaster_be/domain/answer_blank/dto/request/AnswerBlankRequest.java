package com.example.englishmaster_be.domain.answer_blank.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AnswerBlankRequest {

    String content;

    Integer position;

    UUID questionId;
}
