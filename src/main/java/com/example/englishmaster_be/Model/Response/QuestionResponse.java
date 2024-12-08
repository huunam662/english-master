package com.example.englishmaster_be.Model.Response;

import com.example.englishmaster_be.DTO.Type.QuestionType;
import com.example.englishmaster_be.Model.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import lombok.experimental.FieldDefaults;
import java.time.LocalDateTime;
import java.util.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class QuestionResponse {

    UUID questionId;

    UUID partId;

    UUID answerCorrect;

    String questionContent;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MM-dd-yyyy hh:mm:ss")
    LocalDateTime createAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MM-dd-yyyy hh:mm:ss")
    LocalDateTime updateAt;

    List<QuestionResponse> questionGroup;

    List<AnswerResponse> listAnswer;

    int questionScore;

    List<ContentResponse> contentList;

    QuestionType questionType;


    public QuestionResponse(Question question) {

        this.questionId = question.getQuestionId();
        this.questionContent = question.getQuestionContent();
        this.questionScore = question.getQuestionScore();
        this.partId = question.getPart().getPartId();
        this.createAt = question.getCreateAt();
        this.updateAt = question.getUpdateAt();
        this.questionType= question.getQuestionType();

        if (Objects.nonNull(question.getAnswers())) {
            this.listAnswer = question.getAnswers().stream().map(
                    AnswerResponse::new
            ).toList();
        }

        if (Objects.nonNull(question.getContentCollection())) {

            this.contentList = question.getContentCollection().stream().map(
                    ContentResponse::new
            ).toList();

        }

    }

    public QuestionResponse(Question question, Answer answerCorrect) {

        if(Objects.nonNull(answerCorrect))
            this.answerCorrect = answerCorrect.getAnswerId();

        this.questionId = question.getQuestionId();
        this.questionContent = question.getQuestionContent();
        this.questionScore = question.getQuestionScore();
        this.partId = question.getPart().getPartId();
        this.createAt = question.getCreateAt();
        this.updateAt = question.getUpdateAt();
        this.questionType = question.getQuestionType();

        // Xử lý danh sách câu trả lời
        if (Objects.nonNull(question.getAnswers())) {
            this.listAnswer = question.getAnswers()
                    .stream()
                    .map(AnswerResponse::new)  // Chuyển đổi sang AnswerResponse
                    .sorted(Comparator.comparing(AnswerResponse::getAnswerId))  // Sắp xếp theo AnswerId
                    .toList();
        }

        if (Objects.nonNull(question.getContentCollection())) {
            this.contentList = question.getContentCollection().stream().map(
                    ContentResponse::new
            ).toList();
        }
    }

}
