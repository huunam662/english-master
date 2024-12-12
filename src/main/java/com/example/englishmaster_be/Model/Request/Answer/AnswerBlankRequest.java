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
public class AnswerBlankRequest {

    String content;

    Integer position;

    UUID questionId;
}
