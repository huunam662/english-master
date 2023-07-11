package com.example.englishmaster_be.Model.Response;

import com.example.englishmaster_be.Component.GetExtension;
import com.example.englishmaster_be.Model.Question;
import com.example.englishmaster_be.Model.Content;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.UUID;


public class QuestionResponse {
    private UUID questionId;
    private String questionContent;
    private int questionScore;
    private JSONArray contentList;
    private UUID questionGroup;
    private UUID partId;
    private String createAt;
    private String updateAt;

    private JSONObject userCreate;

    private JSONObject userUpdate;

    public QuestionResponse(Question question){
        String link;

        this.questionId = question.getQuestionId();
        this.questionContent = question.getQuestionContent();
        this.questionScore = question.getQuestionScore();

        if(question.getQuestionGroup() == null){
            this.questionGroup = null;
        }
        else{
            this.questionGroup = question.getQuestionGroup().getQuestionId();
        }

        this.partId = question.getPart().getPartId();

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
        this.createAt = sdf.format(Timestamp.valueOf(question.getCreateAt()));
        this.updateAt = sdf.format(Timestamp.valueOf(question.getUpdateAt()));

        contentList = new JSONArray();
        userCreate = new JSONObject();
        userUpdate = new JSONObject();

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


        userCreate.put("User Id", question.getUserCreate().getUserId());
        userCreate.put("User Name", question.getUserCreate().getName());

        userUpdate.put("User Id", question.getUserUpdate().getUserId());
        userUpdate.put("User Name", question.getUserUpdate().getName());
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

	public UUID getQuestionGroup() {
		return questionGroup;
	}

	public void setQuestionGroup(UUID questionGroup) {
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

	public JSONObject getUserCreate() {
		return userCreate;
	}

	public void setUserCreate(JSONObject userCreate) {
		this.userCreate = userCreate;
	}

	public JSONObject getUserUpdate() {
		return userUpdate;
	}

	public void setUserUpdate(JSONObject userUpdate) {
		this.userUpdate = userUpdate;
	}
    
    
}
