package com.example.englishmaster_be.domain.question.dto.response;

import com.example.englishmaster_be.domain.answer.dto.response.AnswerResponse;
import com.example.englishmaster_be.domain.content.dto.response.ContentResponse;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class QuestionAdminResponse {

    UUID questionId;

    UUID answerCorrect;

    UUID partId;

    String questionContent;

    String createAt;

    String updateAt;

    List<AnswerResponse> listAnswer;

    List<QuestionResponse> questionGroup;

    Integer questionScore;

    List<ContentResponse> contentList;

}
