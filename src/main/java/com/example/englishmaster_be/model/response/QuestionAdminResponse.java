package com.example.englishmaster_be.model.response;

import com.example.englishmaster_be.model.Answer;
import com.example.englishmaster_be.model.Content;
import com.example.englishmaster_be.model.Question;
import lombok.Getter;
import lombok.Setter;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@SuppressWarnings("unchecked")
public class QuestionAdminResponse {
    private UUID questionId;
    private String questionContent;
    private int questionScore;
    private JSONArray contentList;
    private List<QuestionResponse> questionGroup;

    private UUID partId;
    private String createAt;
    private String updateAt;

    private List<AnswerResponse> listAnswer;
    private UUID answerCorrect;

    public QuestionAdminResponse(Question question, Answer answerCorrect) {

        this.questionId = question.getQuestionId();
        this.questionContent = question.getQuestionContent();
        this.questionScore = question.getQuestionScore();

        this.answerCorrect = answerCorrect.getAnswerId();

        this.partId = question.getPart().getPartId();

        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy hh:mm:ss");
        this.createAt = sdf.format(Timestamp.valueOf(question.getCreateAt()));
        this.updateAt = sdf.format(Timestamp.valueOf(question.getUpdateAt()));

        if (question.getAnswers() != null) {
            List<AnswerResponse> listAnswerResponse = new ArrayList<>();
            for (Answer answer : question.getAnswers()) {
                listAnswerResponse.add(new AnswerResponse(answer));
            }
            this.listAnswer = listAnswerResponse;
        }

        contentList = new JSONArray();

        if (question.getContentCollection() != null) {
            for (Content content1 : question.getContentCollection()) {
                JSONObject content = new JSONObject();
                content.put("Content Type", content1.getContentType());

                content.put("Content Data", content1.getContentData());

                contentList.add(content);
            }
        }
    }

}
