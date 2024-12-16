package com.example.englishmaster_be.domain.user_answer.dto.response;

import com.example.englishmaster_be.domain.question.dto.response.QuestionBasicResponse;
import com.example.englishmaster_be.domain.user.dto.response.UserBasicResponse;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserAnswerBlankResponse {

    UUID id;

    String answer;

    Integer position;

    QuestionBasicResponse question;

    UserBasicResponse user;

}
