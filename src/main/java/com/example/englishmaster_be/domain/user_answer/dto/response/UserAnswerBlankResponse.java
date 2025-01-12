package com.example.englishmaster_be.domain.user_answer.dto.response;

import com.example.englishmaster_be.domain.answer_blank.dto.response.AnswerBlankResponse;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserAnswerBlankResponse {


    List<AnswerBlankResponse> values;

    List<AnswerBlankResponse> expected;

}
