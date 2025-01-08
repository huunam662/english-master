package com.example.englishmaster_be.domain.user_answer.dto.response;

import com.example.englishmaster_be.domain.answer_blank.dto.response.AnswerBlankResponse;
import com.example.englishmaster_be.domain.question.dto.response.QuestionBasicResponse;
import com.example.englishmaster_be.domain.user.dto.response.UserBasicResponse;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;
import java.util.Map;
import java.util.UUID;

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
