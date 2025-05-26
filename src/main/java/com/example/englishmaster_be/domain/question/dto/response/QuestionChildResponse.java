package com.example.englishmaster_be.domain.question.dto.response;

import com.example.englishmaster_be.domain.answer.dto.response.AnswerResponse;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class QuestionChildResponse extends QuestionResponse{

    List<AnswerResponse> answers;

}
