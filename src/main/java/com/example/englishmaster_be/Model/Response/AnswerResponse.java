package com.example.englishmaster_be.Model.Response;

import com.example.englishmaster_be.Model.Answer;
import org.json.simple.JSONObject;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.UUID;

public class AnswerResponse {
    private UUID questionId;
    private UUID answerId;
    private String answerContent;
    private String createAt;
    private String updateAt;

    private JSONObject userCreate;

    private JSONObject userUpdate;

    public AnswerResponse(Answer answer) {
        questionId = answer.getQuestion().getQuestionId();
        answerId = answer.getAnswerId();
        answerContent = answer.getAnswerContent();

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
        createAt = sdf.format(Timestamp.valueOf(answer.getCreateAt()));
        updateAt = sdf.format(Timestamp.valueOf(answer.getUpdateAt()));

        userCreate = new JSONObject();
        userUpdate = new JSONObject();

        userCreate.put("User Id", answer.getUserCreate().getUserId());
        userCreate.put("User Name", answer.getUserCreate().getName());

        userUpdate.put("User Id", answer.getUserUpdate().getUserId());
        userUpdate.put("User Name", answer.getUserUpdate().getName());
    }

	public UUID getQuestionId() {
		return questionId;
	}

	public void setQuestionId(UUID questionId) {
		this.questionId = questionId;
	}

	public UUID getAnswerId() {
		return answerId;
	}

	public void setAnswerId(UUID answerId) {
		this.answerId = answerId;
	}

	public String getAnswerContent() {
		return answerContent;
	}

	public void setAnswerContent(String answerContent) {
		this.answerContent = answerContent;
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
