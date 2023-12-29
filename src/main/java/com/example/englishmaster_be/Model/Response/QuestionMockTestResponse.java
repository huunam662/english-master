package com.example.englishmaster_be.Model.Response;

import com.example.englishmaster_be.Helper.GetExtension;
import com.example.englishmaster_be.Model.Answer;
import com.example.englishmaster_be.Model.Content;
import com.example.englishmaster_be.Model.Question;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@SuppressWarnings("unchecked")
public class QuestionMockTestResponse {
    private UUID questionId;
    private String questionContent;
    private int questionScore;
    private JSONArray contentList;
    private List<QuestionMockTestResponse> questionGroup;

    private UUID partId;
    private String createAt;
    private String updateAt;

    private List<AnswerResponse> listAnswer;
    private UUID answerCorrect;
    private UUID answerChoice;

    public QuestionMockTestResponse(Question question, Answer answerChoice, Answer answerCorrect){
        String link;

        this.questionId = question.getQuestionId();
        this.questionContent = question.getQuestionContent();
        this.questionScore = question.getQuestionScore();
        if(answerChoice != null){
            this.answerChoice = answerChoice.getAnswerId();
        }

        this.answerCorrect = answerCorrect.getAnswerId();

        this.partId = question.getPart().getPartId();

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
        this.createAt = sdf.format(Timestamp.valueOf(question.getCreateAt()));
        this.updateAt = sdf.format(Timestamp.valueOf(question.getUpdateAt()));

        if(question.getAnswers() != null){
            List<AnswerResponse> listAnswerResponse = new ArrayList<>();
            for(Answer answer: question.getAnswers()){
                listAnswerResponse.add(new AnswerResponse(answer));
            }
            this.listAnswer = listAnswerResponse;
        }

        contentList = new JSONArray();

        if(question.getContentCollection() != null){
            for(Content content1 : question.getContentCollection()){
                JSONObject content = new JSONObject();
                content.put("Content Type", content1.getContentType());

                if(content1.getContentData() == null){
                    content.put("Content Data", content1.getContentData());

                }else {
                    link = GetExtension.linkName(content1.getContentData());
                    content.put("Content Data", link + content1.getContentData());
                }

                contentList.add(content);
            }
        }

    }

    public QuestionMockTestResponse(Question question){
        String link;

        this.questionId = question.getQuestionId();
        this.questionContent = question.getQuestionContent();
        this.questionScore = question.getQuestionScore();

        this.partId = question.getPart().getPartId();

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
        this.createAt = sdf.format(Timestamp.valueOf(question.getCreateAt()));
        this.updateAt = sdf.format(Timestamp.valueOf(question.getUpdateAt()));

        if(question.getAnswers() != null){
            List<AnswerResponse> listAnswerResponse = new ArrayList<>();
            for(Answer answer: question.getAnswers()){
                listAnswerResponse.add(new AnswerResponse(answer));
            }
            this.listAnswer = listAnswerResponse;
        }

        contentList = new JSONArray();

        if(question.getContentCollection() != null){
            for(Content content1 : question.getContentCollection()){
                JSONObject content = new JSONObject();
                content.put("Content Type", content1.getContentType());

                if(content1.getContentData() == null){
                    content.put("Content Data", content1.getContentData());

                }else {
                    link = GetExtension.linkName(content1.getContentData());
                    content.put("Content Data", link + content1.getContentData());
                }

                contentList.add(content);
            }
        }

    }

    public UUID getQuestionId() {
        return questionId;
    }

    public void setQuestionId(UUID questionId) {
        this.questionId = questionId;
    }

    public String getQuestionContent() {
        return questionContent;
    }

    public void setQuestionContent(String questionContent) {
        this.questionContent = questionContent;
    }

    public int getQuestionScore() {
        return questionScore;
    }

    public void setQuestionScore(int questionScore) {
        this.questionScore = questionScore;
    }

    public JSONArray getContentList() {
        return contentList;
    }

    public void setContentList(JSONArray contentList) {
        this.contentList = contentList;
    }

    public List<QuestionMockTestResponse> getQuestionGroup() {
        return questionGroup;
    }

    public void setQuestionGroup(List<QuestionMockTestResponse> questionGroup) {
        this.questionGroup = questionGroup;
    }

    public UUID getPartId() {
        return partId;
    }

    public void setPartId(UUID partId) {
        this.partId = partId;
    }

    public String getCreateAt() {
        return createAt;
    }

    public void setCreateAt(String createAt) {
        this.createAt = createAt;
    }

    public String getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(String updateAt) {
        this.updateAt = updateAt;
    }

    public List<AnswerResponse> getListAnswer() {
        return listAnswer;
    }

    public void setListAnswer(List<AnswerResponse> listAnswer) {
        this.listAnswer = listAnswer;
    }

    public UUID getAnswerCorrect() {
        return answerCorrect;
    }

    public void setAnswerCorrect(UUID answerCorrect) {
        this.answerCorrect = answerCorrect;
    }

    public UUID getAnswerChoice() {
        return answerChoice;
    }

    public void setAnswerChoice(UUID answerChoice) {
        this.answerChoice = answerChoice;
    }
}
