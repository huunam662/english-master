package com.example.englishmaster_be.domain.question.dto.response;

import com.example.englishmaster_be.common.constant.QuestionTypeEnum;
import com.example.englishmaster_be.domain.answer.dto.response.AnswerResponse;
import com.example.englishmaster_be.domain.content.dto.response.ContentBasicResponse;
import com.example.englishmaster_be.domain.excel_fill.dto.response.ExcelQuestionResponse;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class QuestionResponse {

    UUID questionId;

    UUID partId;

    UUID topicId;

    UUID answerCorrectId;

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

    List<QuestionResponse> questionsChildren;
}
