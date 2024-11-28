package com.example.englishmaster_be.Model.Response;

import com.example.englishmaster_be.Model.Answer;
import com.example.englishmaster_be.Model.Question;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Objects;
import java.util.UUID;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@SuppressWarnings("unchecked")
public class QuestionMockTestResponse {

    UUID questionId;

    UUID answerCorrect;

    UUID answerChoice;

    UUID partId;

    String questionContent;

    String createAt;

    String updateAt;

    List<QuestionMockTestResponse> questionGroup;

    List<AnswerResponse> listAnswer;

    int questionScore;

    JSONArray contentList;

    public QuestionMockTestResponse(Question question, Answer answerChoice, Answer answerCorrect) {

        if(Objects.nonNull(answerCorrect))
            this.answerCorrect = answerCorrect.getAnswerId();

        if (Objects.nonNull(answerChoice))
            this.answerChoice = answerChoice.getAnswerId();

        this.questionId = question.getQuestionId();
        this.questionContent = question.getQuestionContent();
        this.questionScore = question.getQuestionScore();
        this.partId = question.getPart().getPartId();

        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy hh:mm:ss");
        if(Objects.nonNull(question.getCreateAt()))
            this.createAt = sdf.format(Timestamp.valueOf(question.getCreateAt()));
        if(Objects.nonNull(question.getUpdateAt()))
            this.updateAt = sdf.format(Timestamp.valueOf(question.getUpdateAt()));

        if (Objects.nonNull(question.getAnswers()))
            this.listAnswer = question.getAnswers().stream().map(AnswerResponse::new).toList();

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

    public QuestionMockTestResponse(Question question) {

        this.questionId = question.getQuestionId();
        this.questionContent = question.getQuestionContent();
        this.questionScore = question.getQuestionScore();
        this.partId = question.getPart().getPartId();

        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy hh:mm:ss");
        this.createAt = sdf.format(Timestamp.valueOf(question.getCreateAt()));
        this.updateAt = sdf.format(Timestamp.valueOf(question.getUpdateAt()));

        if (Objects.nonNull(question.getAnswers()))
            this.listAnswer = question.getAnswers().stream().map(AnswerResponse::new).toList();

        this.contentList = new JSONArray();

        if (Objects.nonNull(question.getContentCollection())) {
            question.getContentCollection().forEach(
                    contentItem -> {

                        JSONObject content = new JSONObject();
                        content.put("Content Type", contentItem.getContentType());
                        content.put("Content Data", contentItem.getContentData());

                        contentList.add(content);
                    }
            );
        }
    }

}
