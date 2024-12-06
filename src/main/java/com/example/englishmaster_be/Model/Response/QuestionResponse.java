package com.example.englishmaster_be.Model.Response;

import com.example.englishmaster_be.DTO.Type.QuestionType;
import com.example.englishmaster_be.Model.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
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

    String createAt;

    String updateAt;

    List<QuestionResponse> questionGroup;

    List<AnswerResponse> listAnswer;

    int questionScore;

    JSONArray contentList;

    QuestionType questionType;


    public QuestionResponse(Question question) {

        this.questionId = question.getQuestionId();
        this.questionContent = question.getQuestionContent();
        this.questionScore = question.getQuestionScore();
        this.partId = question.getPart().getPartId();
        this.questionType= question.getQuestionType();
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy hh:mm:ss");
        this.createAt = sdf.format(Timestamp.valueOf(question.getCreateAt()));
        this.updateAt = sdf.format(Timestamp.valueOf(question.getUpdateAt()));

        if (Objects.nonNull(question.getAnswers())) {
            this.listAnswer = question.getAnswers().stream().map(
                    AnswerResponse::new
            ).toList();
        }

        this.contentList = new JSONArray();

        if (Objects.nonNull(question.getContentCollection())) {

                question.getContentCollection().forEach(
                contentItem -> {
                        JSONObject content = new JSONObject();

                        content.put("Content Type", contentItem.getContentType());

                        content.put("Content Data", contentItem.getContentData());

                        this.contentList.add(content);
                    }
                );
            
        }

    }

    public QuestionResponse(Question question, Answer answerCorrect) {

        if(Objects.nonNull(answerCorrect))
            this.answerCorrect = answerCorrect.getAnswerId();

        this.questionId = question.getQuestionId();
        this.questionContent = question.getQuestionContent();
        this.questionScore = question.getQuestionScore();
        this.partId = question.getPart().getPartId();

        this.questionType = question.getQuestionType();

        // Định dạng ngày tạo và ngày cập nhật
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy hh:mm:ss");
        if(Objects.nonNull(question.getCreateAt()))
            this.createAt = sdf.format(Timestamp.valueOf(question.getCreateAt()));
        if(Objects.nonNull(question.getUpdateAt()))
            this.updateAt = sdf.format(Timestamp.valueOf(question.getUpdateAt()));

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
            question.getContentCollection().forEach(
                contentItem -> {
                    JSONObject contentJson = new JSONObject();
                    contentJson.put("Content Type", contentItem.getContentType());

                    if(Objects.nonNull(contentItem.getContentData()))
                        contentJson.put("Content Data", contentItem.getContentData());
                    else contentJson.put("Content Data", "");

                    this.contentList.add(contentJson);
                }
            );
        }
    }

}
