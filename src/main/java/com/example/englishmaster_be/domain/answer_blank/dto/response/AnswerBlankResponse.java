package com.example.englishmaster_be.domain.answer_blank.dto.response;

import com.example.englishmaster_be.domain.question.dto.response.QuestionResponse;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AnswerBlankResponse {


    Integer position;

    String answer;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    QuestionResponse question;
}
