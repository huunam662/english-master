package com.example.englishmaster_be.domain.user_answer.dto.response;

import com.example.englishmaster_be.domain.answer_matching.dto.response.AnswerMatchingResponse;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserAnswerMatchingResponse {
    List<AnswerMatchingResponse> values;
    List<AnswerMatchingResponse> expected;
}
