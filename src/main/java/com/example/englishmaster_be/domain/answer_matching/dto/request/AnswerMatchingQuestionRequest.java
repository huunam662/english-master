package com.example.englishmaster_be.domain.answer_matching.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AnswerMatchingQuestionRequest {

    String contentLeft;

    String contentRight;

    UUID questionId;
}
