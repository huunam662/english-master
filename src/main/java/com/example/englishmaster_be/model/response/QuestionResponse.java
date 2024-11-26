package com.example.englishmaster_be.model.response;

import com.example.englishmaster_be.model.*;
import lombok.Getter;
import lombok.Setter;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Getter
@Setter
@SuppressWarnings("unchecked")
public class QuestionResponse {
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

    public QuestionResponse(Question question) {

        this.questionId = question.getQuestionId();
        this.questionContent = question.getQuestionContent();
        this.questionScore = question.getQuestionScore();


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
        if (!question.getContentCollection().isEmpty()) {
            for (Content content1 : question.getContentCollection()) {
                JSONObject content = new JSONObject();
                content.put("Content Type", content1.getContentType());

                if (content1.getContentData() == null) {
                    content.put("Content Data", null);

                } else {
                    content.put("Content Data", content1.getContentData());
                }

                contentList.add(content);
            }
        }

    }

    public QuestionResponse(Question question, Answer answerCorrect) {
        this.questionId = question.getQuestionId();
        this.questionContent = question.getQuestionContent();
        this.questionScore = question.getQuestionScore();
        this.answerCorrect = answerCorrect.getAnswerId();
        this.partId = question.getPart().getPartId();

        // Định dạng ngày tạo và ngày cập nhật
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy hh:mm:ss");
        this.createAt = question.getCreateAt() != null ? sdf.format(Timestamp.valueOf(question.getCreateAt())) : null;
        this.updateAt = question.getUpdateAt() != null ? sdf.format(Timestamp.valueOf(question.getUpdateAt())) : null;

        // Xử lý danh sách câu trả lời
        if (question.getAnswers() != null && !question.getAnswers().isEmpty()) {
            this.listAnswer = question.getAnswers()
                    .stream()
                    .map(AnswerResponse::new)  // Chuyển đổi sang AnswerResponse
                    .sorted(Comparator.comparing(AnswerResponse::getAnswerId))  // Sắp xếp theo AnswerId
                    .collect(Collectors.toList());
        }

        // Xử lý content
        this.contentList = new JSONArray();
        if (question.getContentCollection() != null && !question.getContentCollection().isEmpty()) {
            for (Content content1 : question.getContentCollection()) {
                JSONObject contentJson = new JSONObject();
                contentJson.put("Content Type", content1.getContentType());

                if (content1.getContentData() != null) {
                    if (content1.getContentData().startsWith("https")) {
                        contentJson.put("Content Data", content1.getContentData());
                    } else {
                        contentJson.put("Content Data", content1.getContentData());
                    }
                } else {
                    contentJson.put("Content Data", "");
                }
                this.contentList.add(contentJson);
            }
        }
    }

}
