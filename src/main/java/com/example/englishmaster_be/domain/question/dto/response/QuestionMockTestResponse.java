package com.example.englishmaster_be.domain.question.dto.response;

import com.example.englishmaster_be.domain.answer.mapper.AnswerMapper;
import com.example.englishmaster_be.domain.answer.model.AnswerEntity;
import com.example.englishmaster_be.domain.question.model.QuestionEntity;
import com.example.englishmaster_be.domain.answer.dto.response.AnswerResponse;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class QuestionMockTestResponse {

    UUID questionId;

    UUID answerCorrect;

    UUID answerChoice;

    UUID partId;

    String questionContent;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MM-dd-yyyy hh:mm:ss")
    LocalDateTime createAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MM-dd-yyyy hh:mm:ss")
    LocalDateTime updateAt;

    Integer questionScore;

    List<QuestionMockTestResponse> questionGroup;

    List<AnswerResponse> listAnswer;

    public QuestionMockTestResponse(QuestionEntity question, AnswerEntity answerChoice, AnswerEntity answerCorrect) {

        this(question);

        if(Objects.nonNull(answerCorrect))
            this.answerCorrect = answerCorrect.getAnswerId();

        if (Objects.nonNull(answerChoice))
            this.answerChoice = answerChoice.getAnswerId();
    }

    public QuestionMockTestResponse(QuestionEntity question) {

        this.questionId = question.getQuestionId();
        this.questionContent = question.getQuestionContent();
        this.questionScore = question.getQuestionScore();
        this.partId = question.getPart().getPartId();
        this.createAt = question.getCreateAt();
        this.updateAt = question.getUpdateAt();

        if (Objects.nonNull(question.getAnswers()))
            this.listAnswer = AnswerMapper.INSTANCE.toAnswerResponseList(question.getAnswers());
    }

}
