package com.example.englishmaster_be.domain.question.dto.response;

import com.example.englishmaster_be.domain.answer.dto.response.AnswerResponse;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class QuestionAnswersResponse extends Question2Response {

    List<AnswerResponse> answers;
}
