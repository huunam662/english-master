package com.example.englishmaster_be.domain.excel_fill.dto.response;

import com.example.englishmaster_be.common.constant.QuestionTypeEnum;
import com.example.englishmaster_be.domain.answer.dto.request.AnswerBasicRequest;
import com.example.englishmaster_be.domain.answer.dto.response.AnswerResponse;
import com.example.englishmaster_be.domain.content.dto.response.ContentBasicResponse;
import com.fasterxml.jackson.annotation.JsonInclude;
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
public class ExcelQuestionResponse {

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

    QuestionTypeEnum questionType;

    List<ContentBasicResponse> contents;

    List<AnswerResponse> answers;

    List<ExcelQuestionResponse> questionsChildren;
}
