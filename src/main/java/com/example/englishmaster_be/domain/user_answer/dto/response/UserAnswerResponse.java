package com.example.englishmaster_be.domain.user_answer.dto.response;

import com.example.englishmaster_be.domain.user.dto.response.UserBasicResponse;
import com.example.englishmaster_be.domain.answer.dto.response.AnswerResponse;
import com.example.englishmaster_be.domain.question.dto.response.QuestionBasicResponse;
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
public class UserAnswerResponse {

    UUID id;

    String content;

    Integer numberChoice;

    QuestionBasicResponse question;

    UserBasicResponse user;

    List<AnswerResponse> answers;

}
