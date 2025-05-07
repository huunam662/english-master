package com.example.englishmaster_be.domain.question.dto.request;

import com.example.englishmaster_be.domain.answer.dto.request.AnswerBasicRequest;
import com.example.englishmaster_be.common.constant.QuestionType;
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
public class QuestionRequest {


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

    List<QuestionRequest> listQuestionChild;

    boolean hasHints;

}
