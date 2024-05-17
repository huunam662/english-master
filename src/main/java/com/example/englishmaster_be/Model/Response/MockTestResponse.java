package com.example.englishmaster_be.Model.Response;

import com.example.englishmaster_be.Model.MockTest;
import org.json.simple.JSONObject;

import java.sql.Time;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

public class MockTestResponse {
    private UUID mockTestID;
    private int score;
    private Time time;
    private UUID topicId;
	private Object user;

    private String createAt;
    private String updateAt;

    private JSONObject userCreate;

    private JSONObject userUpdate;

    public MockTestResponse(MockTest mockTest) {
        this.mockTestID = mockTest.getMockTestId();
        this.score = mockTest.getScore();
        this.time = mockTest.getTime();
        this.topicId = mockTest.getTopic().getTopicId();

		Map<String, Object> userObj = new HashMap<>();
		userObj.put("user_id", mockTest.getUser().getUserId());
		userObj.put("user_name",  mockTest.getUser().getName());
		this.user = userObj;

        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy hh:mm:ss");
        this.createAt = sdf.format(Timestamp.valueOf(mockTest.getCreateAt()));
        this.updateAt = sdf.format(Timestamp.valueOf(mockTest.getUpdateAt()));

        userCreate = new JSONObject();
        userUpdate = new JSONObject();

        userCreate.put("User Id", mockTest.getUserCreate().getUserId());
        userCreate.put("User Name", mockTest.getUserCreate().getName());

        userUpdate.put("User Id", mockTest.getUserUpdate().getUserId());
        userUpdate.put("User Name", mockTest.getUserUpdate().getName());
    }

	public UUID getMockTestID() {
		return mockTestID;
	}

	public void setMockTestID(UUID mockTestID) {
		this.mockTestID = mockTestID;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public Time getTime() {
		return time;
	}

	public void setTime(Time time) {
		this.time = time;
	}

	public UUID getTopicId() {
		return topicId;
	}

	public void setTopicId(UUID topicId) {
		this.topicId = topicId;
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

	public Object getUser() {
		return user;
	}

	public void setUser(Object user) {
		this.user = user;
	}
}
