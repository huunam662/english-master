package com.example.englishmaster_be.domain.user_answer.dto.response;

import com.example.englishmaster_be.domain.answer_matching.dto.response.AnswerMatchingBasicResponse;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserAnswerScoreResponse {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    List<AnswerMatchingBasicResponse> answers;

    Integer scoreAnswer = 0;

}
