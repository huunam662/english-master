package com.example.englishmaster_be.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AnswerBlankResponse {

    UUID id;

    Integer position;

    String answer;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    QuestionResponse question;
}
