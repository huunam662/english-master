package com.example.englishmaster_be.domain.user_answer.dto.request;

import com.example.englishmaster_be.common.constant.QuestionTypeEnum;
import com.example.englishmaster_be.domain.answer_matching.dto.response.AnswerMatchingBasicResponse;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserAnswerRequest {
    UUID questionId;

    QuestionTypeEnum type;

    List<String> answer;

    List<Object> answersBlank;

    List<AnswerMatchingBasicResponse> answersMatching;

}
