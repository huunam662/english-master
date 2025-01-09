package com.example.englishmaster_be.domain.user_answer.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AnswerDetailsResponse {

    UUID id;

    String questionContent;
    String type;

    Object answers;
}
