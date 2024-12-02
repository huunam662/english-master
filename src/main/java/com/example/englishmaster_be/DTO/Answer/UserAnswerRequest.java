package com.example.englishmaster_be.DTO.Answer;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserAnswerRequest {

    UUID userId;

    UUID questionId;

    UUID answerId;

    String content;

    int position;
}
