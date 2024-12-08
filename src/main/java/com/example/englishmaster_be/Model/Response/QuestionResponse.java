package com.example.englishmaster_be.Model.Response;

import com.example.englishmaster_be.Model.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.cglib.core.Local;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@SuppressWarnings("unchecked")
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


    public QuestionResponse(Question question) {

        this.questionId = question.getQuestionId();
        this.questionContent = question.getQuestionContent();
        this.questionScore = question.getQuestionScore();
        this.partId = question.getPart().getPartId();

        this.createAt = question.getCreateAt();
        this.updateAt = question.getUpdateAt();

        if (Objects.nonNull(question.getAnswers())) {
            this.listAnswer = question.getAnswers().stream().map(
                    AnswerResponse::new
            ).toList();
        }

        this.contentList = new JSONArray();

        if (Objects.nonNull(question.getContentCollection())) {

                this.contentList = question.getContentCollection().stream().map(
                        content -> new ContentResponse(content)
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

        // Xử lý danh sách câu trả lời
        if (Objects.nonNull(question.getAnswers())) {
            this.listAnswer = question.getAnswers()
                    .stream()
                    .map(AnswerResponse::new)  // Chuyển đổi sang AnswerResponse
                    .sorted(Comparator.comparing(AnswerResponse::getAnswerId))  // Sắp xếp theo AnswerId
                    .toList();
        }

        // Xử lý Content
        this.contentList = new JSONArray();

        if (Objects.nonNull(question.getContentCollection())) {
            this.contentList = question.getContentCollection().stream().map(
                    content -> new ContentResponse(content)
            ).toList();
        }
    }

}
