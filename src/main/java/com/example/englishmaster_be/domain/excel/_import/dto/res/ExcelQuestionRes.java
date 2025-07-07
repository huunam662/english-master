package com.example.englishmaster_be.domain.excel._import.dto.res;

import com.example.englishmaster_be.common.constant.QuestionType;
import com.example.englishmaster_be.domain.answer.dto.response.AnswerResponse;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ExcelQuestionRes {

    UUID questionId;

    UUID partId;

    UUID topicId;

    String questionContent;

    String questionResult;

    String questionExplainEn;

    String questionExplainVn;

    String contentAudio;

    String contentImage;

    Integer questionScore;

    Boolean isQuestionParent;

    QuestionType questionType;

    List<AnswerResponse> answers;

    List<ExcelQuestionRes> questionsChildren;
}
