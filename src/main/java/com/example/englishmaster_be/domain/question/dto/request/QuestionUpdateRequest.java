package com.example.englishmaster_be.domain.question.dto.request;

import com.example.englishmaster_be.common.constant.QuestionType;
import com.example.englishmaster_be.domain.answer.dto.request.AnswerBasicRequest;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

import java.util.List;
import java.util.UUID;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class QuestionUpdateRequest {

    UUID questionId;

    UUID partId;

    String questionContent;

    String questionExplainEn;

    String questionExplainVn;

    Integer questionScore;

    Integer numberChoice;

    QuestionType questionType;

    String contentImage;

    String contentAudio;

    List<AnswerBasicRequest> listAnswer;

    List<QuestionUpdateRequest> listQuestionChild;

    boolean hasHints;

}
