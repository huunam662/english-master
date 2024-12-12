package com.example.englishmaster_be.Model.Request.Answer;

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
